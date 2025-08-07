package de.muenchen.rbs.kitafinderdatenservice.domain.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportError;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KindmappeDTO;

@Mapper
public interface ExportErrorMapper {

	ExportErrorMapper INSTANCE = Mappers.getMapper(ExportErrorMapper.class);

	@Mapping(target = "id", expression = "java(new de.muenchen.rbs.kitafinderdatenservice.domain.ExportId(km.getId(), exportId))")
	@Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "errorMessage", expression = "java(message)")
	ExportError kindmappeToExportError(KindmappeDTO km, @Context Integer exportId, @Context String message);

	@Mapping(target = "id", expression = "java(new de.muenchen.rbs.kitafinderdatenservice.domain.ExportId(id.getId(), exportId))")
	@Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "errorMessage", expression = "java(message)")
	ExportError idToExportError(KindmappeId id, @Context Integer exportId, @Context String message);

}