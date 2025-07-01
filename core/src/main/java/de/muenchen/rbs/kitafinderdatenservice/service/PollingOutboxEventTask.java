package de.muenchen.rbs.kitafinderdatenservice.service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import de.muenchen.rbs.kitafinderdatenservice.domain.events.OutboxState;
import de.muenchen.rbs.kitafinderdatenservice.domain.events.Outboxevent;
import de.muenchen.rbs.kitafinderdatenservice.repository.OutboxeventRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

/**
 * Scheduled Task, der Events in der Outbox "verschickt".
 * 
 * @author michael.prankl
 *
 */
@Component
@Getter
@Setter
@Slf4j
@ConditionalOnProperty(value = "app.outbox.polling.enabled")
public class PollingOutboxEventTask {

	private int maxBatchSize;
	private final OutboxeventRepository outboxRepo;
	private final OutboxEventHandlerDelegator outboxEventConsumer;

	public PollingOutboxEventTask(@Value("${app.outbox.polling.max-batch-size:100}") int maxBatchSize,
			OutboxeventRepository outboxRepo, OutboxEventHandlerDelegator outboxEventConsumer) {
		super();
		this.maxBatchSize = maxBatchSize;
		this.outboxRepo = outboxRepo;
		this.outboxEventConsumer = outboxEventConsumer;

		log.info("Initialisiere PollingOutboxEventTask mit max-batch-size={}.", maxBatchSize);
	}

	/**
	 * Versucht, Events aus der Outbox zu "konsumieren". Wird periodisch über eine
	 * konfigurierbaren Scheduler ausgeführt.
	 */
	@Scheduled(initialDelayString = "${app.outbox.polling.initial-delay:PT30S}", fixedDelayString = "${app.outbox.polling.fixed-delay:PT30S}")
	@SchedulerLock(name = "pollingOutboxEventConsumer", lockAtLeastFor = "${app.outbox.polling.lock-minimum:PT5S}")
	@Transactional
	public void consumePendingEvents() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		log.debug("Polling for consumable OutboxEvents ...");
		Stream<Outboxevent> eventStream = outboxRepo.findAllByStateOrderByTimestamp(EnumSet.of(OutboxState.PENDING),
				LocalDateTime.now());
		AtomicLong atomicLong = new AtomicLong(0);
		eventStream.limit(maxBatchSize).forEachOrdered(event -> {
			log.debug("Passing to event consumer delegator: {}", event);
			outboxEventConsumer.consumeEvent(event);
			atomicLong.getAndIncrement();
		});
		log.debug("Polling and processing of {} outbox events took {} ms.", atomicLong.get(),
				stopWatch.getTotalTimeMillis());
	}

}
