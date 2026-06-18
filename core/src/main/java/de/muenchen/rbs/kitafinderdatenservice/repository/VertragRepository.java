package de.muenchen.rbs.kitafinderdatenservice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportId;
import de.muenchen.rbs.kitafinderdatenservice.domain.Vertrag;

public interface VertragRepository
		extends PagingAndSortingRepository<Vertrag, ExportId>, CrudRepository<Vertrag, ExportId> {

	@Modifying
	int deleteByExportId(Long exportId);

	@Query(value = "SELECT * FROM VERTRAG WHERE id = :id AND export_id = (SELECT MAX(ID) FROM EXPORT_RUN)", nativeQuery = true)
	Optional<Vertrag> findMostRecentById(Long id);

	@Query(value = """
			SELECT * FROM VERTRAG V
			JOIN KINDDATEN K ON V.ID = K.ID AND V.EXPORT_ID = K.EXPORT_ID
			WHERE V.EXPORT_ID = (SELECT MAX(R.ID) FROM EXPORT_RUN R)
			AND V.ID NOT IN (SELECT VERTRAG.ID FROM VERTRAG WHERE VERTRAG.EXPORT_ID = (SELECT MAX(R.ID)-1 FROM EXPORT_RUN R))
			ORDER BY V.ID ASC""", nativeQuery = true)
	Page<Vertrag> findNew(Pageable page);
}
