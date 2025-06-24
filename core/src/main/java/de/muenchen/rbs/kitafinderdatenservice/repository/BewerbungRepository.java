package de.muenchen.rbs.kitafinderdatenservice.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.muenchen.rbs.kitafinderdatenservice.domain.Bewerbung;
import de.muenchen.rbs.kitafinderdatenservice.domain.ExportId;

public interface BewerbungRepository
		extends PagingAndSortingRepository<Bewerbung, ExportId>, CrudRepository<Bewerbung, ExportId> {

	@Modifying
	int deleteByExportId(Integer exportId);

}
