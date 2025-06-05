package de.muenchen.rbs.kitafinderdatenservice.domain;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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
	private int geschlechtId;
	private Boolean pflegekind;
	private int immunisierungMasernId;
	private int wohnhaftBeiId;
	private String familiensprache;
	private String weitereFamiliensprachen;
	private Boolean koerperlicheBehinderung;
	private Boolean geistigeBehinderung;
	private Boolean seelischeBehinderung;
	private KindakteStatus status;
	private String statusDatum;
	private String statusGrund;
	private Integer absagegrundId;
	private String externeId;
	private String exportdatum;
	private String kibigId;
	private String anmeldedatum;
	private String voranmeldungGueltigBis;
	private String erstvorstellung;
	private boolean persoenlichVorgestellt;
	private String betreuungswunschAb;
	private int betreuungswunschZeitId;
	private int betreuungsformId;
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
	private int butVerwendungszweckId;
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
