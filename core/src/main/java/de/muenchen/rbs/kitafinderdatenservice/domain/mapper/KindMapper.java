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

@Mapper(uses = DateMapper.class)
public interface KindMapper {

	KindMapper INSTANCE = Mappers.getMapper(KindMapper.class);

	default Kind kindmappeToKind(KindmappeDTO km, @Context Integer exportId) {
		Kind kind = new Kind();

		kind.setId(km.getId());
		kind.setExportId(exportId);
		kind.setTimestamp(LocalDateTime.now());

		if (km.getKindAkten() != null && km.getKindAkten().size() > 0) {
			List<Bewerbung> bewerbungen = new ArrayList<>();
			List<Vertrag> vertraege = new ArrayList<>();

			KindDatenstand master = null;
			for (KindakteDTO ka : km.getKindAkten()) {
				KindDatenstand stand = this.kindakteToKindDatenstand(ka, exportId, kind);

				if (stand instanceof Vertrag v) {
					vertraege.add(v);
				} else if (stand instanceof Bewerbung b) {
					bewerbungen.add(b);
				}

				// masterkind ermitteln
				if (ka.isMasterkind() && (master == null
						|| master.getPlatzart().getSortOrder() < stand.getPlatzart().getSortOrder())) {
					master = stand;
				}
			}
			kind.setBewerbungen(bewerbungen);
			kind.setVertraege(vertraege);

			// TODO: prüfen, ob das so benötigt wird und Sinn macht. Fallback falls kein
			// Masterkind angegeben ist.
			if (master == null) {
				master = kind.getKinddaten().get(0);
			}
			kind.setMasterkindId(master.getId());
		}

		return kind;
	}

	@Mapping(target = "exportId", expression = "java(exportId)")
	@Mapping(target = "sorgeberechtigter1.exportId", expression = "java(exportId)")
	@Mapping(target = "sorgeberechtigter2.exportId", expression = "java(exportId)")
	@Mapping(target = "status", expression = "java(de.muenchen.rbs.kitafinderdatenservice.domain.KindakteStatus.getFromKitafinderId(kindakte.getStatusId()))")
	@Mapping(target = "geschlecht", source = "geschlechtId")
	@Mapping(target = "immunisierungMasern", source = "immunisierungMasernId")
	@Mapping(target = "wohnhaftBei", source = "wohnhaftBeiId")
	@Mapping(target = "betreuungswunschZeit", source = "betreuungswunschZeitId")
	@Mapping(target = "betreuungsform", source = "betreuungsformId")
	@Mapping(target = "absagegrund", source = "absagegrundId")
	@Mapping(target = "butVerwendungszweck", source = "butVerwendungszweckId")
	@Mapping(target = "platzart", source = "platzartId")
	@Mapping(target = "kind", expression = "java(kind)")
	Bewerbung kindakteToBewerbung(KindakteDTO kindakte, @Context Integer exportId, @Context Kind kind);

	@Mapping(target = "exportId", expression = "java(exportId)")
	@Mapping(target = "sorgeberechtigter1.exportId", expression = "java(exportId)")
	@Mapping(target = "sorgeberechtigter2.exportId", expression = "java(exportId)")
	@Mapping(target = "status", expression = "java(de.muenchen.rbs.kitafinderdatenservice.domain.KindakteStatus.getFromKitafinderId(kindakte.getStatusId()))")
	@Mapping(target = "geschlecht", source = "geschlechtId")
	@Mapping(target = "immunisierungMasern", source = "immunisierungMasernId")
	@Mapping(target = "wohnhaftBei", source = "wohnhaftBeiId")
	@Mapping(target = "betreuungswunschZeit", source = "betreuungswunschZeitId")
	@Mapping(target = "betreuungsform", source = "betreuungsformId")
	@Mapping(target = "absagegrund", source = "absagegrundId")
	@Mapping(target = "butVerwendungszweck", source = "butVerwendungszweckId")
	@Mapping(target = "platzart", source = "platzartId")
	@Mapping(target = "kind", expression = "java(kind)")
	Vertrag kindakteToVertrag(KindakteDTO kindakte, @Context Integer exportId, @Context Kind kind);

	default KindDatenstand kindakteToKindDatenstand(KindakteDTO kindakte, @Context Integer exportId,
			@Context Kind kind) {
		if (kindakte.getStatusId() == 4 || kindakte.getStatusId() == 5) {
			return kindakteToVertrag(kindakte, exportId, kind);
		} else {
			return kindakteToBewerbung(kindakte, exportId, kind);
		}
	}

}