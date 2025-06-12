package de.muenchen.rbs.kitafinderdatenservice.domain.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import de.muenchen.rbs.kitafinderdatenservice.domain.Bewerbung;
import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindDatenstand;
import de.muenchen.rbs.kitafinderdatenservice.domain.Vertrag;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KindakteDTO;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KindmappeDTO;

@Mapper
public interface KindMapper {

	KindMapper INSTANCE = Mappers.getMapper(KindMapper.class);

	default Kind kindmappeToKind(KindmappeDTO km, @Context Integer exportId) {
		Kind kind = new Kind();

		kind.setId(km.getId());
		kind.setExportId(exportId);
		kind.setTimestamp(LocalDateTime.now());

		if (km.getKindAkten() != null && km.getKindAkten().size() > 0) {
			KindakteDTO master = km.getKindAkten().getFirst();
			kind.setMasterkindId(master.getId());

			List<Bewerbung> bewerbungen = new ArrayList<>();
			List<Vertrag> vertraege = new ArrayList<>();

			for (KindakteDTO ka : km.getKindAkten()) {
				KindDatenstand stand = this.kindakteToKindDatenstand(ka, exportId, kind);

				if (stand instanceof Vertrag v) {
					vertraege.add(v);
				} else if (stand instanceof Bewerbung b) {
					bewerbungen.add(b);
				}
			}

			kind.setBewerbungen(bewerbungen);
			kind.setVertraege(vertraege);
		}

		return kind;
	}

	@Mapping(target = "id", expression = "java(new de.muenchen.rbs.kitafinderdatenservice.domain.ExportId(kindakte.getId(), exportId))")
	@Mapping(target = "status", expression = "java(de.muenchen.rbs.kitafinderdatenservice.domain.KindakteStatus.getFromKitafinderId(kindakte.getStatusId()))")
	@Mapping(target = "geschlecht", source = "geschlechtId")
	@Mapping(target = "immunisierungMasern", source = "immunisierungMasernId")
	@Mapping(target = "wohnhaftBei", source = "wohnhaftBeiId")
	@Mapping(target = "betreuungswunschZeit", source = "betreuungswunschZeitId")
	@Mapping(target = "betreuungsform", source = "betreuungsformId")
	@Mapping(target = "absagegrund", source = "absagegrundId")
	@Mapping(target = "butVerwendungszweck", source = "butVerwendungszweckId")
	@Mapping(target = "kind", expression = "java(kind)")
	Bewerbung kindakteToBewerbung(KindakteDTO kindakte, @Context Integer exportId, @Context Kind kind);

	@Mapping(target = "id", expression = "java(new de.muenchen.rbs.kitafinderdatenservice.domain.ExportId(kindakte.getId(), exportId))")
	@Mapping(target = "status", expression = "java(de.muenchen.rbs.kitafinderdatenservice.domain.KindakteStatus.getFromKitafinderId(kindakte.getStatusId()))")
	@Mapping(target = "geschlecht", source = "geschlechtId")
	@Mapping(target = "immunisierungMasern", source = "immunisierungMasernId")
	@Mapping(target = "wohnhaftBei", source = "wohnhaftBeiId")
	@Mapping(target = "betreuungswunschZeit", source = "betreuungswunschZeitId")
	@Mapping(target = "betreuungsform", source = "betreuungsformId")
	@Mapping(target = "absagegrund", source = "absagegrundId")
	@Mapping(target = "butVerwendungszweck", source = "butVerwendungszweckId")
	@Mapping(target = "kind", expression = "java(kind)")
	Vertrag kindakteToVertrag(KindakteDTO kindakte, @Context Integer exportId, @Context Kind kind);

	default KindDatenstand kindakteToKindDatenstand(KindakteDTO kindakte, @Context Integer exportId, @Context Kind kind) {
		if (kindakte.getStatusId() == 4 || kindakte.getStatusId() == 5) {
			return kindakteToVertrag(kindakte, exportId, kind);
		} else {
			return kindakteToBewerbung(kindakte, exportId, kind);
		}
	}

}