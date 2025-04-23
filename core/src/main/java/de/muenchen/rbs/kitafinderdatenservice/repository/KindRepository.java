package de.muenchen.rbs.kitafinderdatenservice.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportId;
import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;

public interface KindRepository extends PagingAndSortingRepository<Kind, ExportId>, CrudRepository<Kind, ExportId> {

	@Query(value = "SELECT * FROM KIND_AKTUELL WHERE id = :id", nativeQuery = true)
	Optional<Kind> findMostRecentById(Integer id);

	@Query(value = "SELECT * FROM KIND_AKTUELL", nativeQuery = true)
	Page<Kind> findAllMostRecent(Pageable page);

	@Modifying
	@Query(value = "DELETE FROM KIND K WHERE K.TIMESTAMP < :ageThreshold AND (ID, TIMESTAMP) NOT IN (SELECT ID, TIMESTAMP FROM KIND_AKTUELL KA)", nativeQuery = true)
	int deleteOldRows(LocalDateTime ageThreshold);

}
