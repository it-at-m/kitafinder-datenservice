package de.muenchen.rbs.kitafinderdatenservice.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class KindDTO {
	private Integer id;
	private Integer exportId;
	private LocalDateTime timestamp;
	
	private String vorname;
	private String nachname;
	private String geburtsdatum;
	
	private String kindAkten;
}
