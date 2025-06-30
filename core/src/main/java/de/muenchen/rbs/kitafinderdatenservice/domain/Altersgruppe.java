package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.time.LocalDate;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Altersgruppe {
	private int id;
	private LocalDate gueltigVon;
	private LocalDate gueltigBis;
}
