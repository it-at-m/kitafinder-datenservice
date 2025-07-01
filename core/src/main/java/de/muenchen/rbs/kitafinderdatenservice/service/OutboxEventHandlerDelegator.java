package de.muenchen.rbs.kitafinderdatenservice.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.muenchen.rbs.kitafinderdatenservice.domain.events.OutboxState;
import de.muenchen.rbs.kitafinderdatenservice.domain.events.Outboxevent;
import de.muenchen.rbs.kitafinderdatenservice.repository.OutboxeventRepository;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * Service, der {@link OutboxEvent}s konsumiert, an entsprechende Handler
 * weitergibt und den Status der Abarbeitung setzt.
 * 
 * @author michael.prankl
 *
 */
@Service
@Slf4j
public class OutboxEventHandlerDelegator {

	private final OutboxeventRepository outboxRepo;
	private final PluginRegistry<EventHandlerDelegate, Outboxevent> plugins;
	private final int maxRetryCount;
	private final Duration backoffDurationBase;

	private Counter successEventCounter;
	private Counter failedEventCounter;

	/**
	 * Erzeugt eine Instanz.
	 * 
	 * @param outboxRepo          ein {@link OutboxEventRepository}
	 * @param plugins             die {@link PluginRegistry} für
	 *                            {@link EventHandlerDelegate}s
	 * @param meterRegistry       eine {@link MeterRegistry}
	 * @param maxRetryCount       max retry count
	 * @param backoffDurationBase Backoff Duration Basis (minimale Backoff Zeit)
	 */
	public OutboxEventHandlerDelegator(OutboxeventRepository outboxRepo,
			PluginRegistry<EventHandlerDelegate, Outboxevent> plugins, MeterRegistry meterRegistry,
			@Value("${app.outbox.retry.max-retries:0}") int maxRetryCount,
			@Value("${app.outbox.retry.backoff-duration-base:PT30S}") Duration backoffDurationBase) {
		this.outboxRepo = outboxRepo;
		this.plugins = plugins;
		this.maxRetryCount = maxRetryCount;
		this.backoffDurationBase = backoffDurationBase;
		this.successEventCounter = Counter.builder("app_outbox_events_success")
				.description("Anzahl der erfolgreichen OutboxEvents Verarbeitungen").register(meterRegistry);
		this.failedEventCounter = Counter.builder("app_outbox_events_failed")
				.description("Anzahl der fehlgeschlagenen OutboxEvent Verarbeitungen").register(meterRegistry);
	}

	/**
	 * Konsumiert das übergebene Event und delegiert die eigentliche Arbeit an ein
	 * passendes Delegates.
	 * 
	 * @param event ein {@link OutboxEvent}
	 */
	@Transactional
	@Timed(value = "app_outbox_events_consume_time", description = "Verarbeitungszeit für die Verarbeitung eines Outbox Events")
	public void consumeEvent(Outboxevent event) {
		log.debug("Consuming event {} ...", event.getId());
		try {
			// lookup handler via spring plugin (strategy pattern implementation)
			Optional<EventHandlerDelegate> handlerPluginOpt = this.plugins.getPluginFor(event);
			if (handlerPluginOpt.isPresent()) {
				handlerPluginOpt.get().consumeEvent(event);
			} else {
				throw new UnsupportedOperationException(
						String.format("Für das Event vom Typ '%s' gibt es keinen Handler.", event.getType()));
			}
			// event got successfully handled
			event.setState(OutboxState.SUCCESSFUL);
			successEventCounter.increment();
			log.debug("Event {} successfully handled.", event.getId());
		} catch (Exception e) {
			event.setErrorMessage(e.getStackTrace().toString());
			// catch all clause to catch all possible errors of handlers
			log.debug(String.format("Consuming of event (id=%s) failed: %s", event.getId(), e.getMessage()), e);
			if (canBeRetried(event)) {
				event.setState(OutboxState.PENDING);
				int nrOfRetries = event.getRetryCount() != null ? event.getRetryCount() : 0;
				event.setRetryNotBefore(computeRetryNotBefore(nrOfRetries));
				event.setRetryCount(nrOfRetries + 1);
				log.info("Failed event (id={}) can be retried (retry count is {}), but not before: {}", event.getId(),
						event.getRetryCount(), event.getRetryNotBefore());
			} else {
				log.error(String.format(
						"Event(id=%s) failed after %s unsuccessful retries - setting state to FAILED."
								+ " No more automatic processing/consuming will take place (dead letter!).",
						event.getId(), event.getRetryCount()), e);
				event.setState(OutboxState.FAILED);
				failedEventCounter.increment();
			}
		}
		outboxRepo.save(event);
	}

	/**
	 * @param nrOfRetries bisher erfolgte, erfolglose Retry Versuche
	 * @return
	 */
	protected LocalDateTime computeRetryNotBefore(int nrOfRetries) {
		// first retry
		int nextRetry = nrOfRetries + 1;
		// exponential backoff with base = 2
		Duration waitTimeForNextRetry = this.backoffDurationBase.multipliedBy((long) Math.pow(2, nextRetry));
		return LocalDateTime.now().plus(waitTimeForNextRetry);
	}

	/**
	 * @param event das Event
	 * @return true, wenn das Event noch geretried werden kann (=maximale Retry
	 *         Versuche noch nicht überschritten)
	 */
	protected boolean canBeRetried(Outboxevent event) {
		return event.getRetryCount() == null || event.getRetryCount() <= this.maxRetryCount;
	}

}
