package de.muenchen.rbs.kitafinderdatenservice.domain.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import de.muenchen.rbs.kitafinderdatenservice.domain.Bewerbung;
import de.muenchen.rbs.kitafinderdatenservice.domain.ExportId;
import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindDatenstand;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindDatenstandKategorie;
import de.muenchen.rbs.kitafinderdatenservice.domain.Vertrag;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.Kindakte;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.Kindmappe;

@Mapper
public interface KindMapper {

	KindMapper INSTANCE = Mappers.getMapper(KindMapper.class);

	default Kind kindmappeToKind(Kindmappe km, @Context Integer exportId) {
		Kind kind = new Kind();

		kind.setId(new ExportId(km.getId(), exportId));
		kind.setTimestamp(LocalDateTime.now());

		if (km.getKindAkten() != null && km.getKindAkten().size() > 0) {
			Kindakte master = km.getKindAkten().getFirst();
			kind.setMasterkindId(master.getId());

			List<Bewerbung> bewerbungen = new ArrayList<>();
			List<Vertrag> vertraege = new ArrayList<>();

			for (Kindakte ka : km.getKindAkten()) {
				KindDatenstand stand = this.kindakteToKindDatenstand(ka);

				if (stand.getStatus().getKategorie() == KindDatenstandKategorie.VERTRAG) {
					vertraege.add(new Vertrag(kind, stand));
				} else {
					bewerbungen.add(new Bewerbung(kind, stand));
				}
			}

			kind.setBewerbungen(bewerbungen);
			kind.setVertraege(vertraege);
		}

		return kind;
	}

	@Mapping(target = "status", expression = "java(de.muenchen.rbs.kitafinderdatenservice.domain.KindakteStatus.getFromKitafinderId(kindakte.getStatusId()))")
	@Mapping(target = "geschlecht", source = "geschlechtId")
	@Mapping(target = "immunisierungMasern", source = "immunisierungMasernId")
	@Mapping(target = "wohnhaftBei", source = "wohnhaftBeiId")
	@Mapping(target = "betreuungswunschZeit", source = "betreuungswunschZeitId")
	@Mapping(target = "betreuungsform", source = "betreuungsformId")
	@Mapping(target = "absagegrund", source = "absagegrundId")
	@Mapping(target = "butVerwendungszweck", source = "butVerwendungszweckId")
	KindDatenstand kindakteToKindDatenstand(Kindakte kindakte);

}