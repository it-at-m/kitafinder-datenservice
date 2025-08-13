package de.muenchen.rbs.kitafinderdatenservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportRun;

public interface ExportRunRepository extends CrudRepository<ExportRun, Integer> {

	@Query(value = "SELECT e FROM ExportRun e WHERE e.status = de.muenchen.rbs.kitafinderdatenservice.domain.ExportStatus.SUCCESS ORDER BY e.startTime DESC")
	List<ExportRun> findAllSuccessfullOrdered();
	
	@Query(value = "SELECT * FROM NEXTVAL('EXPORT_ID_SEQUENCE')", nativeQuery = true)
	long getNextId();

}
