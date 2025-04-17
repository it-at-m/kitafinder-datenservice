package de.muenchen.rbs.kitafinderdatenservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.TimedId;

public interface KindRepository extends PagingAndSortingRepository<Kind, TimedId>, CrudRepository<Kind, TimedId> {

	@Query(value = "SELECT * FROM KIND_AKTUELL WHERE id = :id", nativeQuery = true)
	Optional<Kind> findMostRecentById(String id);

}
