package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import lombok.Data;

@Data
public class Integration {
	private int id;
	private String gestellt;
	private String abgelehnt;
	private String von;
	private String bis;
	private String bemerkung;
	private int bescheiderlassendeStelleId;
	private boolean gewichtungsfaktorIntegration;
}
