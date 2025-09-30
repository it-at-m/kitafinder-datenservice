package de.muenchen.rbs.kitafinderdatenservice.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.Vertrag;
import de.muenchen.rbs.kitafinderdatenservice.domain.events.Outboxevent;
import de.muenchen.rbs.kitafinderdatenservice.service.OutboxeventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class VertragEventProcessor implements ItemProcessor<Vertrag, Outboxevent> {

	private OutboxeventService outboxeventService;

	@Override
	public Outboxevent process(Vertrag vertrag) throws Exception {
		return outboxeventService.buildNewVertrag(vertrag);
	}

}
