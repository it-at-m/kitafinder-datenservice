package de.muenchen.rbs.kitafinderdatenservice.domain.mapper;

import java.time.LocalDateTime;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportId;
import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.Kindakte;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.Kindmappe;

@Mapper
public interface KindMapper {

	KindMapper INSTANCE = Mappers.getMapper(KindMapper.class);

	default Kind kindmappeToKind(Kindmappe km, @Context Integer exportId) {
		Kind kind = new Kind();

		kind.setId(new ExportId(km.getId(), exportId));
		kind.setTimestamp(LocalDateTime.now());
		kind.setKindAkten(km.getKindAkten().toString());
		
		// TODO
		if (km.getKindAkten() != null && km.getKindAkten().size() > 0) {
			Kindakte master = km.getKindAkten().getFirst();
			
			kind.setVorname(master.getVorname());
			kind.setNachname(master.getNachname());
			kind.setGeburtsdatum(master.getGeburtsdatum());
		}

		return kind;
	}

}