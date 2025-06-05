package de.muenchen.rbs.kitafinderdatenservice.domain;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
public class KindDatenstand {
	@JsonAlias("kindakteId")
	@Column(name = "KINDAKTE_ID")
	private int id;
	private String vorname;
	private String nachname;
	private String geburtsdatum;
	@Enumerated(EnumType.STRING)
	private Geschlecht geschlecht;
	private Boolean pflegekind;
	@Enumerated(EnumType.STRING)
	private ImmunisierungMasern immunisierungMasern;
	@Enumerated(EnumType.STRING)
	private WohnhaftBei wohnhaftBei;
	private String familiensprache;
	private String weitereFamiliensprachen;
	private Boolean koerperlicheBehinderung;
	private Boolean geistigeBehinderung;
	private Boolean seelischeBehinderung;
	@Enumerated(EnumType.STRING)
	private KindakteStatus status;
	private String statusDatum;
	private String statusGrund;
	@Enumerated(EnumType.STRING)
	private Absagegrund absagegrund;
	private String externeId;
	private String exportdatum;
	private String kibigId;
	private String anmeldedatum;
	private String voranmeldungGueltigBis;
	private String erstvorstellung;
	private boolean persoenlichVorgestellt;
	private String betreuungswunschAb;
	@Enumerated(EnumType.STRING)
	private Betreuungszeit betreuungswunschZeit;
	@Enumerated(EnumType.STRING)
	private Betreuungsform betreuungsform;
	private Integer prioWarteliste;
	private int platzartId;
	private String anmeldecode;
	private boolean integrativplatzGewuenscht;
	private boolean platzsharingGewuenscht;
	private long betreuungszeitVon;
	private long betreuungszeitBis;
	private String bemerkungenZurBewerbung;
	private boolean wechselkind;
	private boolean wechselkindAngabeEltern;
	private Integer prioEltern;
	private int schulbezirkId;
	private int jahrgangsstufeId;
	private String umzugInSchulbezirkZum;
	private boolean gastschulantragGestellt;
	private String vertragsbeginn;
	private String ersterBetreuungstag;
	private String kuendigungsdatum;
	private String vertragsende;
	private String vertragsendeSpaetestens;
	private String einschulungstermin;
	private boolean auswaertig;
	private boolean umzugGeplant;
	private String auswaertigSeit;
	private String auswaertigBis;
	private boolean auswaertigGefoerdert;
	private String ausflugsfoerderungVon;
	private String ausflugsfoerderungBis;
	private String butId;
	@Enumerated(EnumType.STRING)
	private ButVerwedungszweck butVerwendungszweck;
	private int kitaId;
	private String kitaName;
	private String kitaKibigId;
//	private Sorgeberechtigter sorgeberechtigter1;
//	private Sorgeberechtigter sorgeberechtigter2;
//	private List<Integer> besondereLageIds;
//	private List<Integer> elternprioritaetsgruendeIds;
//	private List<Altersgruppe> altersgruppen;
//	private List<Vertrag> verträge;
//	private List<BringAbholzeit> bringAbholzeiten;
//	private List<Gruppe> gruppen;
//	private List<Integration> integrationen;
//	private List<Kontingent> kontingente;
}
