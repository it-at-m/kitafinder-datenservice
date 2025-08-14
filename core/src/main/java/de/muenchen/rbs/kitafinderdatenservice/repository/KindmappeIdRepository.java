package de.muenchen.rbs.kitafinderdatenservice.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;

public interface KindmappeIdRepository
		extends PagingAndSortingRepository<KindmappeId, Long>, CrudRepository<KindmappeId, Long> {

	@Override
	@Modifying
	@Query("DELETE FROM KindmappeId")
	void deleteAll();

}
