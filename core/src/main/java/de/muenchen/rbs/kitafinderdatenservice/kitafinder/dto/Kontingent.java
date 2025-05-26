package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import lombok.Data;

@Data
public class Kontingent {
	private int id;
	private String von;
	private String bis;
	private String bemerkung;
}
