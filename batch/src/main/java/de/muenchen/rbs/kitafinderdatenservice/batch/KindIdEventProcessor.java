package de.muenchen.rbs.kitafinderdatenservice.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.events.Outboxevent;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;
import de.muenchen.rbs.kitafinderdatenservice.service.OutboxeventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class KindIdEventProcessor implements ItemProcessor<Long, Outboxevent> {

	private KindRepository kindRepository;
	private OutboxeventService outboxeventService;

	@Override
	public Outboxevent process(Long kindId) throws Exception {
		Kind kind = kindRepository.findMostRecentById(kindId).get();
		return outboxeventService.buildKindCreated(kind);
	}

}
