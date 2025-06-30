package de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class AltersgruppeDTO {
	private int id;
	private String gueltigVon;
	private String gueltigBis;
}
