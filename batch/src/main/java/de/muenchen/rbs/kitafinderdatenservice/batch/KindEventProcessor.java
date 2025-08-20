package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
public class KindEventProcessor implements ItemProcessor<Kind, List<Outboxevent>> {

	private KindRepository kindRepository;
	private OutboxeventService outboxeventService;

	@Override
	public List<Outboxevent> process(Kind kind) throws Exception {
		List<Outboxevent> events = new ArrayList<>();

		Optional<Kind> oldKind = kindRepository.findAktuellById(kind.getId());

		if (oldKind.isEmpty()) {
			events.add(outboxeventService.buildKindCreated(kind));
		} else {
			kind.getVertraege().stream().filter(
					newV -> oldKind.get().getVertraege().stream().noneMatch(old -> old.getId().equals(newV.getId())))
					.forEach(newV -> events.add(outboxeventService.buildNewVertrag(newV)));
		}

		return events;
	}

}
