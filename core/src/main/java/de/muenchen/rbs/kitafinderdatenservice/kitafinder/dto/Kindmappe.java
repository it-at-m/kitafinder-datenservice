package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import java.util.List;

import lombok.Data;

@Data
public class Kindmappe {
	private Integer id;
	private boolean isGefunden;
	private List<Kindakte> kindAkten;
	private ErmittlungsDauer ermittlungsDauer;
}
