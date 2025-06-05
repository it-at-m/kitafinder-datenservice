package de.muenchen.rbs.kitafinderdatenservice.domain.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import de.muenchen.rbs.kitafinderdatenservice.domain.Bewerbung;
import de.muenchen.rbs.kitafinderdatenservice.domain.ExportId;
import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindDatenstand;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindakteStatus;
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
				if (ka.getStatusId() == 4 || ka.getStatusId() == 5) {
					vertraege.add(new Vertrag(kind, this.kindakteToKindDatenstand(ka)));
				} else {
					bewerbungen.add(new Bewerbung(kind, this.kindakteToKindDatenstand(ka)));
				}
			}

			kind.setBewerbungen(bewerbungen);
			kind.setVertraege(vertraege);
		}

		return kind;
	}

	KindDatenstand kindakteToKindDatenstand(Kindakte kindakte);

	default KindakteStatus kindakteToKindDatenstand(int statusId) {
		switch (statusId) {
		case 0:
			return KindakteStatus.ABSAGE;
		case 1:
			return KindakteStatus.WARTELISTE;
		case 2:
			return KindakteStatus.ZUSAGE;
		case 3:
			return KindakteStatus.VORGEMERKT;
		case 4:
			return KindakteStatus.VERTRAG;
		case 5:
			return KindakteStatus.VERTRAG_GEKUENDIGT;
		case 7:
			return KindakteStatus.ZUSAGE_DURCH_ELTERN;
		case 8:
			return KindakteStatus.IN_PRUEFUNG;
		case 98:
			return KindakteStatus.NICHT_FREIGESCHALTET;
		case 99:
			return KindakteStatus.VERWORFEN;
		default:
			return KindakteStatus.UNBEKANNT;
		}
	}

}