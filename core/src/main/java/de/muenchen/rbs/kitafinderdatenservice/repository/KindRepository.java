package de.muenchen.rbs.kitafinderdatenservice.repository;

import org.springframework.data.repository.CrudRepository;

import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.TimedId;

public interface KindRepository extends CrudRepository<Kind, TimedId> {

}
