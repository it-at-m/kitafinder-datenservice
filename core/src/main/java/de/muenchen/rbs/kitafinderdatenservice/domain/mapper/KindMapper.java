package de.muenchen.rbs.kitafinderdatenservice.domain.mapper;

import java.time.LocalDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.TimedId;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.Kindmappe;

@Mapper
public interface KindMapper {

	KindMapper INSTANCE = Mappers.getMapper(KindMapper.class);

	Kind kindmappeToKind(Kindmappe car);

	default TimedId personToPersonDto(String id) {
		return new TimedId(id, LocalDateTime.now());
	}

}