package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.Sorgeberechtigter;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "KINDDATEN")
@NoArgsConstructor
public class KindDatenstand {

	@JsonIgnore
	@EmbeddedId
	private ExportId id;

	@ManyToOne
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@JoinColumns({ @JoinColumn(name = "kindId", referencedColumnName = "id"),
			@JoinColumn(name = "kindExportId", referencedColumnName = "exportId") })
	private Kind kind;

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

	@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "SB1_ID")),
        @AttributeOverride(name = "anrede", column = @Column(name = "SB1_ANREDE")),
        @AttributeOverride(name = "titel", column = @Column(name = "SB1_TITEL")),
        @AttributeOverride(name = "vorname", column = @Column(name = "SB1_VORNAME")),
        @AttributeOverride(name = "nachname", column = @Column(name = "SB1_NACHNAME")),
        @AttributeOverride(name = "strasse", column = @Column(name = "SB1_STRASSE")),
        @AttributeOverride(name = "hausnummer", column = @Column(name = "SB1_HAUSNUMMER")),
        @AttributeOverride(name = "plz", column = @Column(name = "SB1_PLZ")),
        @AttributeOverride(name = "ort", column = @Column(name = "SB1_ORT")),
        @AttributeOverride(name = "land", column = @Column(name = "SB1_LAND")),
        @AttributeOverride(name = "telefon1", column = @Column(name = "SB1_TELEFON1")),
        @AttributeOverride(name = "telefon2", column = @Column(name = "SB1_TELEFON2")),
        @AttributeOverride(name = "email", column = @Column(name = "SB1_EMAIL")),
        @AttributeOverride(name = "auslaendischeherkunft", column = @Column(name = "SB1_AUSLAENDISCHEHERKUNFT")),
        @AttributeOverride(name = "nichtDeutschsprachigeHerkunft", column = @Column(name = "SB1_NICHT_DEUTSCHSPRACHIGE_HERKUNFT")),
        @AttributeOverride(name = "alleinErziehend", column = @Column(name = "SB1_ALLEINERZIEHEND")),
        @AttributeOverride(name = "geplanteAufnahmeBerufstaetigkeit", column = @Column(name = "SB1_GEPLANTE_AUFNAHME_BERUFSTAETIGKEIT")),
        @AttributeOverride(name = "geplanteAufnahmeBerufstaetigkeitAb", column = @Column(name = "SB1_GEPLANTE_AUFNAHME_BERUFSTAETIGKEIT_AB")),
        @AttributeOverride(name = "geplantAufnahmeAusbildung", column = @Column(name = "SB1_GEPLANT_AUFNAHME_AUSBILDUNG")),
        @AttributeOverride(name = "geplantAufnahmeAusbildungAb", column = @Column(name = "SB1_GEPLANT_AUFNAHME_AUSBILDUNG_AB")),
        @AttributeOverride(name = "geplantAufnahmeStudium", column = @Column(name = "SB1_GEPLANT_AUFNAHME_STUDIUM")),
        @AttributeOverride(name = "geplantAufnahmeStudiumAb", column = @Column(name = "SB1_GEPLANT_AUFNAHME_STUDIUM_AB")),
        @AttributeOverride(name = "berufstaetig", column = @Column(name = "SB1_BERUFSTAETIG")),
        @AttributeOverride(name = "erstStudiumAusbildung", column = @Column(name = "SB1_ERST_STUDIUM_AUSBILDUNG")),
        @AttributeOverride(name = "wochenarbeitszeit", column = @Column(name = "SB1_WOCHENARBEITSZEIT")),
        @AttributeOverride(name = "wochenarbeitstage", column = @Column(name = "SB1_WOCHENARBEITSTAGE")),
        @AttributeOverride(name = "lageDerArbeitszeitId", column = @Column(name = "SB1_LAGE_DER_ARBEITSZEIT_ID")),
        @AttributeOverride(name = "arbeitsuchend", column = @Column(name = "SB1_ARBEITSUCHEND")),
        @AttributeOverride(name = "arbeitsuchendSeit", column = @Column(name = "SB1_ARBEITSUCHEND_SEIT"))
    })
	@Embedded
	private Sorgeberechtigter sorgeberechtigter1;

	@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "SB2_ID")),
        @AttributeOverride(name = "anrede", column = @Column(name = "SB2_ANREDE")),
        @AttributeOverride(name = "titel", column = @Column(name = "SB2_TITEL")),
        @AttributeOverride(name = "vorname", column = @Column(name = "SB2_VORNAME")),
        @AttributeOverride(name = "nachname", column = @Column(name = "SB2_NACHNAME")),
        @AttributeOverride(name = "strasse", column = @Column(name = "SB2_STRASSE")),
        @AttributeOverride(name = "hausnummer", column = @Column(name = "SB2_HAUSNUMMER")),
        @AttributeOverride(name = "plz", column = @Column(name = "SB2_PLZ")),
        @AttributeOverride(name = "ort", column = @Column(name = "SB2_ORT")),
        @AttributeOverride(name = "land", column = @Column(name = "SB2_LAND")),
        @AttributeOverride(name = "telefon1", column = @Column(name = "SB2_TELEFON1")),
        @AttributeOverride(name = "telefon2", column = @Column(name = "SB2_TELEFON2")),
        @AttributeOverride(name = "email", column = @Column(name = "SB2_EMAIL")),
        @AttributeOverride(name = "auslaendischeherkunft", column = @Column(name = "SB2_AUSLAENDISCHEHERKUNFT")),
        @AttributeOverride(name = "nichtDeutschsprachigeHerkunft", column = @Column(name = "SB2_NICHT_DEUTSCHSPRACHIGE_HERKUNFT")),
        @AttributeOverride(name = "alleinErziehend", column = @Column(name = "SB2_ALLEINERZIEHEND")),
        @AttributeOverride(name = "geplanteAufnahmeBerufstaetigkeit", column = @Column(name = "SB2_GEPLANTE_AUFNAHME_BERUFSTAETIGKEIT")),
        @AttributeOverride(name = "geplanteAufnahmeBerufstaetigkeitAb", column = @Column(name = "SB2_GEPLANTE_AUFNAHME_BERUFSTAETIGKEIT_AB")),
        @AttributeOverride(name = "geplantAufnahmeAusbildung", column = @Column(name = "SB2_GEPLANT_AUFNAHME_AUSBILDUNG")),
        @AttributeOverride(name = "geplantAufnahmeAusbildungAb", column = @Column(name = "SB2_GEPLANT_AUFNAHME_AUSBILDUNG_AB")),
        @AttributeOverride(name = "geplantAufnahmeStudium", column = @Column(name = "SB2_GEPLANT_AUFNAHME_STUDIUM")),
        @AttributeOverride(name = "geplantAufnahmeStudiumAb", column = @Column(name = "SB2_GEPLANT_AUFNAHME_STUDIUM_AB")),
        @AttributeOverride(name = "berufstaetig", column = @Column(name = "SB2_BERUFSTAETIG")),
        @AttributeOverride(name = "erstStudiumAusbildung", column = @Column(name = "SB2_ERST_STUDIUM_AUSBILDUNG")),
        @AttributeOverride(name = "wochenarbeitszeit", column = @Column(name = "SB2_WOCHENARBEITSZEIT")),
        @AttributeOverride(name = "wochenarbeitstage", column = @Column(name = "SB2_WOCHENARBEITSTAGE")),
        @AttributeOverride(name = "lageDerArbeitszeitId", column = @Column(name = "SB2_LAGE_DER_ARBEITSZEIT_ID")),
        @AttributeOverride(name = "arbeitsuchend", column = @Column(name = "SB2_ARBEITSUCHEND")),
        @AttributeOverride(name = "arbeitsuchendSeit", column = @Column(name = "SB2_ARBEITSUCHEND_SEIT"))
    })
	@Embedded
	private Sorgeberechtigter sorgeberechtigter2;

	@ElementCollection(targetClass = Integer.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "BESONDERE_LAGE", joinColumns = { @JoinColumn(name = "id"),
			@JoinColumn(name = "exportId") })
	@Column(name = "BESONDERE_LAGE_ID")
	private List<Integer> besondereLageIds;

	@ElementCollection(targetClass = Integer.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "ELTERNPRIORITAETSGRUND", joinColumns = { @JoinColumn(name = "id"),
			@JoinColumn(name = "exportId") })
	@Column(name = "ELTERNPRIORITAETSGRUND_ID")
	private List<Integer> elternprioritaetsgruendeIds;

//	private List<Altersgruppe> altersgruppen;
//	private List<Vertrag> verträge;
//	private List<BringAbholzeit> bringAbholzeiten;
//	private List<Gruppe> gruppen;
//	private List<Integration> integrationen;
//	private List<Kontingent> kontingente;

}
