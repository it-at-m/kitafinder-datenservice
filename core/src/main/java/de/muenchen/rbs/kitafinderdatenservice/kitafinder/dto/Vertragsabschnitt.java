package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import lombok.Data;

@Data
public class Vertragsabschnitt {
	private int id;
	private String ab;
	private String bis;
	private int betreuungszeitId;
	private int verpflegungId;
	private Boolean koerperlicheBehinderung;
	private Boolean geistigeBehinderung;
	private Boolean seelischeBehinderung;
	private int bringAbholzeitId;
}
