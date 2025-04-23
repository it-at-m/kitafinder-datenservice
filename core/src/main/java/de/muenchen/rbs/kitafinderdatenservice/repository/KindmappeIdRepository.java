package de.muenchen.rbs.kitafinderdatenservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;

public interface KindmappeIdRepository
		extends PagingAndSortingRepository<KindmappeId, Integer>, CrudRepository<KindmappeId, Integer> {

}
