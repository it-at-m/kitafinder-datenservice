package de.muenchen.rbs.kitafinderdatenservice.domain;

import static de.muenchen.rbs.kitafinderdatenservice.domain.KindDatenstandKategorie.BEWERBUNG;

import lombok.Getter;

@Getter
public enum KindakteStatus {
	ABSAGE(BEWERBUNG),
	WARTELISTE(BEWERBUNG),
	ZUSAGE(BEWERBUNG),
	VORGEMERKT(BEWERBUNG),
	VERTRAG(KindDatenstandKategorie.VERTRAG),
	VERTRAG_GEKUENDIGT(KindDatenstandKategorie.VERTRAG),
	ZUSAGE_DURCH_ELTERN(BEWERBUNG),
	IN_PRUEFUNG(BEWERBUNG),
	NICHT_FREIGESCHALTET(BEWERBUNG),
	VERWORFEN(BEWERBUNG),
	UNBEKANNT(BEWERBUNG);
	
	private final KindDatenstandKategorie kategorie;
	
	private KindakteStatus(KindDatenstandKategorie kategorie) {
		this.kategorie = kategorie;
	}
	
	public static KindakteStatus getFromKitafinderId(int kitafinderId) {
		switch (kitafinderId) {
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
