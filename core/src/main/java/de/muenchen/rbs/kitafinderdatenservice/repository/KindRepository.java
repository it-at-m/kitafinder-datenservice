package de.muenchen.rbs.kitafinderdatenservice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportId;
import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;

public interface KindRepository extends PagingAndSortingRepository<Kind, ExportId>, CrudRepository<Kind, ExportId> {

	@Query(value = "SELECT * FROM KIND_AKTUELL WHERE id = :id", nativeQuery = true)
	Optional<Kind> findAktuellById(Long id);

	@Query(value = "SELECT * FROM KIND WHERE id = :id AND export_id = (SELECT MAX(ID) FROM EXPORT_RUN)", nativeQuery = true)
	Optional<Kind> findMostRecentById(Long id);

	@Query(value = "SELECT * FROM KIND_AKTUELL", nativeQuery = true)
	Page<Kind> findAllAktuell(Pageable page);

	@Query(value = "SELECT * FROM KIND WHERE export_id = (SELECT MAX(ID) FROM EXPORT_RUN)", nativeQuery = true)
	Page<Kind> findAllMostRecent(Pageable page);

	@Query(value = "SELECT delete_by_export_id(:exportId)", nativeQuery = true)
	int deleteByExportId(Long exportId);

	@Query(value = """
			SELECT * FROM KIND
			WHERE KIND.EXPORT_ID = (SELECT MAX(R.ID) FROM EXPORT_RUN R)
			AND ID NOT IN (SELECT KIND_AKTUELL.ID FROM KIND_AKTUELL)
			ORDER BY ID ASC""", nativeQuery = true)
	Page<Kind> findNew(Pageable page);
}
