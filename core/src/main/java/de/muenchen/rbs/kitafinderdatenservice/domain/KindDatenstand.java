package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.Cascade;

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
	private Long id;
	@Id
	private Long exportId;

	private Long kindId;
	@ManyToOne
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
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
	private LocalDate statusDatum;
	private String statusGrund;
	@Enumerated(EnumType.STRING)
	private Absagegrund absagegrund;
	private String externeId;
	private LocalDate exportdatum;
	private String kibigId;
	private LocalDate anmeldedatum;
	private LocalDate voranmeldungGueltigBis;
	private LocalDate erstvorstellung;
	private boolean persoenlichVorgestellt;
	private LocalDate betreuungswunschAb;
	@Enumerated(EnumType.STRING)
	private Betreuungszeit betreuungswunschZeit;
	@Enumerated(EnumType.STRING)
	private Betreuungsform betreuungsform;
	private Long prioWarteliste;
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
	private Long prioEltern;
	private int schulbezirkId;
	private int jahrgangsstufeId;
	private LocalDate umzugInSchulbezirkZum;
	private boolean gastschulantragGestellt;
	private LocalDate vertragsbeginn;
	private LocalDate ersterBetreuungstag;
	private LocalDate kuendigungsdatum;
	private LocalDate vertragsende;
	private LocalDate vertragsendeSpaetestens;
	private LocalDate einschulungstermin;
	private boolean auswaertig;
	private boolean umzugGeplant;
	private LocalDate auswaertigSeit;
	private LocalDate auswaertigBis;
	private boolean auswaertigGefoerdert;
	private LocalDate ausflugsfoerderungVon;
	private LocalDate ausflugsfoerderungBis;
	private String butId;
	@Enumerated(EnumType.STRING)
	private ButVerwedungszweck butVerwendungszweck;
	private int kitaId;
	private String kitaName;
	private String kitaKibigId;

	private Long sb1_id;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "sb1_id", referencedColumnName = "id", insertable = false, updatable = false)
	@JoinColumn(name = "exportId", referencedColumnName = "exportId", insertable = false, updatable = false)
	private Sorgeberechtigter sorgeberechtigter1;

	public void setSorgeberechtigter1(Sorgeberechtigter sb) {
		this.sorgeberechtigter1 = sb;
		this.setSb1_id(sb.getId());
	}

	private Long sb2_id;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "sb2_id", referencedColumnName = "id", insertable = false, updatable = false)
	@JoinColumn(name = "exportId", referencedColumnName = "exportId", insertable = false, updatable = false)
	private Sorgeberechtigter sorgeberechtigter2;

	public void setSorgeberechtigter2(Sorgeberechtigter sb) {
		this.sorgeberechtigter2 = sb;
		this.setSb2_id(sb.getId());
	}

	@ElementCollection(targetClass = Long.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "BESONDERE_LAGE", joinColumns = { @JoinColumn(name = "exportId"), @JoinColumn(name = "kinddatenId") })
	@Column(name = "BESONDERE_LAGE_ID")
	private List<Long> besondereLageIds;

	@ElementCollection(targetClass = Long.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "ELTERNPRIORITAETSGRUND", joinColumns = { @JoinColumn(name = "exportId"), @JoinColumn(name = "kinddatenId") })
	@Column(name = "ELTERNPRIORITAETSGRUND_ID")
	private List<Long> elternprioritaetsgruendeIds;

	@ElementCollection(targetClass = Altersgruppe.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "ALTERSGRUPPE", joinColumns = { @JoinColumn(name = "exportId"), @JoinColumn(name = "kinddatenId") })
	private List<Altersgruppe> altersgruppen;

	@ElementCollection(targetClass = BringAbholzeit.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "BRING_ABHOLZEIT", joinColumns = { @JoinColumn(name = "exportId"), @JoinColumn(name = "kinddatenId") })
	private List<BringAbholzeit> bringAbholzeiten;
	
	@ElementCollection(targetClass = Gruppe.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "GRUPPE", joinColumns = { @JoinColumn(name = "exportId"), @JoinColumn(name = "kinddatenId") })
	private List<Gruppe> gruppen;

	@ElementCollection(targetClass = Integration.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "INTEGRATION", joinColumns = { @JoinColumn(name = "exportId"), @JoinColumn(name = "kinddatenId") })
	private List<Integration> integrationen;

	@ElementCollection(targetClass = Kontingent.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "KONTINGENT", joinColumns = { @JoinColumn(name = "exportId"), @JoinColumn(name = "kinddatenId") })
	private List<Kontingent> kontingente;

}
