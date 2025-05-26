package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import lombok.Data;

@Data
public class ErmittlungsDauer {
	private int gesamt;
	private int akten;
	private int besondereLage;
	private int elternprioritaetsgruende;
	private int altersgruppen;
	private int vertraege;
	private int bringAbholzeiten;
	private int gruppen;
	private int integration;
	private int kontingente;
}
