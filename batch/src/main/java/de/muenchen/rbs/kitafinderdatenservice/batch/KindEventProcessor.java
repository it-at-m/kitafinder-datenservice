package de.muenchen.rbs.kitafinderdatenservice.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.Vertrag;
import de.muenchen.rbs.kitafinderdatenservice.domain.events.Outboxevent;
import de.muenchen.rbs.kitafinderdatenservice.service.OutboxeventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class KindEventProcessor implements ItemProcessor<Kind, Outboxevent> {

	private OutboxeventService outboxeventService;

	@Override
	public Outboxevent process(Kind kind) throws Exception {
		return outboxeventService.buildKindCreated(kind);
	}

}
