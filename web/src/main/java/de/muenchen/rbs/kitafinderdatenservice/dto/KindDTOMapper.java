package de.muenchen.rbs.kitafinderdatenservice.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;

@Mapper
public interface KindDTOMapper {

	KindDTOMapper INSTANCE = Mappers.getMapper(KindDTOMapper.class);

	KindDTO kindToKindDTO(Kind kind);

}