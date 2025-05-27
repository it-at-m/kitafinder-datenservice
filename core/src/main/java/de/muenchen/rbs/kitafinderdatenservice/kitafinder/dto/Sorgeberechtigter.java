package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import lombok.Data;

@Data
public class Sorgeberechtigter {
	private int id;
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
	private String geplanteAufnahmeBerufstaetigkeitAb;
	private Boolean geplantAufnahmeAusbildung;
	private String geplantAufnahmeAusbildungAb;
	private Boolean geplantAufnahmeStudium;
	private String geplantAufnahmeStudiumAb;
	private Boolean berufstaetig;
	private Boolean erstStudiumAusbildung;
	private Double wochenarbeitszeit;
	private Double wochenarbeitstage;
	private Integer lageDerArbeitszeitId;
	private Boolean arbeitsuchend;
	private String arbeitsuchendSeit;
}