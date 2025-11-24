package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.events.Outboxevent;
import de.muenchen.rbs.kitafinderdatenservice.repository.OutboxEventRepository;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class OutboxeventBatchWriter implements ItemWriter<List<Outboxevent>> {

	private OutboxEventRepository eventRepository;

	@Override
	public void write(Chunk<? extends List<Outboxevent>> eventsChunk) throws Exception {
		List<Outboxevent> events = eventsChunk.getItems().stream().flatMap(List::stream).toList();

		eventRepository.saveAll(events);
	}

}
