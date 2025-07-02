package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.time.LocalDate;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "SORGEBERECHTIGTER")
public class Sorgeberechtigter {
	@Id
	private Integer id;
	@Id
	private Integer exportId;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@OneToMany(mappedBy = "sorgeberechtigter1", cascade = CascadeType.ALL, orphanRemoval = true)
	private Collection<KindDatenstand> kinddaten;

	private String anrede;
	private String titel;
	private String vorname;
	private String nachname;
	private String strasse;
	private String hausnummer;
	private String plz;
	private String ort;
	private String land;
	private String telefon1;
	private String telefon2;
	private String email;
	private Boolean auslaendischeherkunft;
	private Boolean nichtDeutschsprachigeHerkunft;
	private Boolean alleinErziehend;
	private Boolean geplanteAufnahmeBerufstaetigkeit;
	private LocalDate geplanteAufnahmeBerufstaetigkeitAb;
	private Boolean geplantAufnahmeAusbildung;
	private LocalDate geplantAufnahmeAusbildungAb;
	private Boolean geplantAufnahmeStudium;
	private LocalDate geplantAufnahmeStudiumAb;
	private Boolean berufstaetig;
	private Boolean erstStudiumAusbildung;
	private Double wochenarbeitszeit;
	private Double wochenarbeitstage;
	private Integer lageDerArbeitszeitId;
	private Boolean arbeitsuchend;
	private LocalDate arbeitsuchendSeit;
}