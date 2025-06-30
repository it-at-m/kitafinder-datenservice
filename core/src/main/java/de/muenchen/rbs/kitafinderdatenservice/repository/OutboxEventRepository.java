package de.muenchen.rbs.kitafinderdatenservice.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.muenchen.rbs.kitafinderdatenservice.domain.events.Outboxevent;

public interface OutboxEventRepository
		extends PagingAndSortingRepository<Outboxevent, UUID>, CrudRepository<Outboxevent, UUID> {

}
