package de.muenchen.rbs.kitafinderdatenservice.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportId;
import de.muenchen.rbs.kitafinderdatenservice.domain.Vertrag;

public interface VertragRepository
		extends PagingAndSortingRepository<Vertrag, ExportId>, CrudRepository<Vertrag, ExportId> {

	@Modifying
	@Query(value = "DELETE FROM Vertrag K WHERE K.exportId = :exportId")
	int deleteByExportId(Integer exportId);

}
