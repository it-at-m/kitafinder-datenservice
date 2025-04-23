package de.muenchen.rbs.kitafinderdatenservice.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;

@Mapper
public interface KindDTOMapper {

	KindDTOMapper INSTANCE = Mappers.getMapper(KindDTOMapper.class);

	@Mapping(target = "id", expression = "java(kind.getId().getId())")
	@Mapping(target = "exportId", expression = "java(kind.getId().getExportId())")
	KindDTO kindToKindDTO(Kind kind);

}