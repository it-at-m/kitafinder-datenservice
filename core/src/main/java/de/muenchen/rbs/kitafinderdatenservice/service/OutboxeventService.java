package de.muenchen.rbs.kitafinderdatenservice.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.Vertrag;
import de.muenchen.rbs.kitafinderdatenservice.domain.events.EventType;
import de.muenchen.rbs.kitafinderdatenservice.domain.events.Outboxevent;
import de.muenchen.rbs.kitafinderdatenservice.repository.OutboxeventRepository;
import jakarta.validation.Valid;

@Service
public class OutboxeventService {

	@Autowired
	private OutboxeventRepository repository;

	@Autowired
	private ObjectMapper mapper;

	public Outboxevent buildKindCreated(Kind kind) {
		Outboxevent event = new Outboxevent();
		event.setType(EventType.KIND_CREATED);
		event.setTimestamp(LocalDateTime.now());
		event.setAggregateType(kind.getClass().getName());
		event.setAggregateId(kind.getId());
		event.setPayload(mapper.valueToTree(kind));

		return event;
	}

	public Outboxevent buildNewVertrag(Vertrag vertrag) {
		Outboxevent event = new Outboxevent();
		event.setType(EventType.NEW_VERTRAG);
		event.setTimestamp(LocalDateTime.now());
		event.setAggregateType(vertrag.getClass().getName());
		event.setAggregateId(vertrag.getId());
		event.setPayload(mapper.valueToTree(vertrag));

		return event;
	}

	public void saveAll(@Valid Iterable<Outboxevent> events) {
		repository.saveAll(events);
	}

}
