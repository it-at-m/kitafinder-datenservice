package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
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
@IdClass(ExportId.class)
public class KindDatenstand {

	@Id
	private Integer id;
	@Id
	private Integer exportId;

	private Integer kindId;
	@ManyToOne
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@JoinColumn(name = "kindId", referencedColumnName = "id", insertable = false, updatable = false)
	@JoinColumn(name = "exportId", referencedColumnName = "exportId", insertable = false, updatable = false)
	private Kind kind;

	public void setKind(Kind kind) {
		this.kind = kind;
		this.setKindId(kind.getId());
	}

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
	@Enumerated(EnumType.STRING)
	private Platzart platzart;
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

	private Integer sb1_id;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "sb1_id", referencedColumnName = "id", insertable = false, updatable = false)
	@JoinColumn(name = "exportId", referencedColumnName = "exportId", insertable = false, updatable = false)
	private Sorgeberechtigter sorgeberechtigter1;

	public void setSorgeberechtigter1(Sorgeberechtigter sb) {
		this.sorgeberechtigter1 = sb;
		this.setSb1_id(sb.getId());
	}

	private Integer sb2_id;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "sb2_id", referencedColumnName = "id", insertable = false, updatable = false)
	@JoinColumn(name = "exportId", referencedColumnName = "exportId", insertable = false, updatable = false)
	private Sorgeberechtigter sorgeberechtigter2;

	public void setSorgeberechtigter2(Sorgeberechtigter sb) {
		this.sorgeberechtigter2 = sb;
		this.setSb2_id(sb.getId());
	}

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
