package de.muenchen.rbs.kitafinderdatenservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportError;
import de.muenchen.rbs.kitafinderdatenservice.domain.ExportId;

public interface ExportErrorRepository
		extends PagingAndSortingRepository<ExportError, ExportId>, CrudRepository<ExportError, ExportId> {

}
