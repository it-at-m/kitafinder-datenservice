package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class KontingentDTO {
	private int id;
	private String von;
	private String bis;
	private String bemerkung;
}
