package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Kindakte {
	private Integer id;
	private String vorname;
	private String nachname;
	@JsonFormat(pattern = "dd.MM.yyyy")
	private LocalDate geburtsdatum;
	private Integer geschlechtId;
	private String externeId;
	private String kibigId;
	private Integer wohnhaftBeiId;
	private Integer statusId;
	private Integer kitaId;
	private String kitaName;

}
