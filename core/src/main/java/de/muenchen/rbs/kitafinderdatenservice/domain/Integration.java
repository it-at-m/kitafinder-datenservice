package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.time.LocalDate;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Integration {
	private int id;
	private String gestellt;
	private String abgelehnt;
	private LocalDate von;
	private LocalDate bis;
	private String bemerkung;
	private int bescheiderlassendeStelleId;
	private boolean gewichtungsfaktorIntegration;
}
