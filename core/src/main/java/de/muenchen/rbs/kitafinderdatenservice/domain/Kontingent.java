package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.time.LocalDate;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Kontingent {
	private int id;
	private LocalDate von;
	private LocalDate bis;
	private String bemerkung;
}
