package de.muenchen.rbs.kitafinderdatenservice.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportId;
import de.muenchen.rbs.kitafinderdatenservice.domain.Vertrag;
import de.muenchen.rbs.kitafinderdatenservice.domain.events.Outboxevent;
import de.muenchen.rbs.kitafinderdatenservice.repository.VertragRepository;
import de.muenchen.rbs.kitafinderdatenservice.service.OutboxeventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class VertragIdEventProcessor implements ItemProcessor<Long, Outboxevent> {

	private VertragRepository vertragRepository;
	private OutboxeventService outboxeventService;

	@Override
	public Outboxevent process(Long vertragId) throws Exception {
		Vertrag vertrag = vertragRepository.findMostRecentById(vertragId).get();
		return outboxeventService.buildNewVertrag(vertrag);
	}

}
