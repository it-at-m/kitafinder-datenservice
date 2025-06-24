package de.muenchen.rbs.kitafinderdatenservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.muenchen.rbs.kitafinderdatenservice.domain.events.KinddatenEvent;

public interface EventRepository
		extends PagingAndSortingRepository<KinddatenEvent, Integer>, CrudRepository<KinddatenEvent, Integer> {

}
