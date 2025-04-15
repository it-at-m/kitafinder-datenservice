package de.muenchen.rbs.kitafinderdatenservice.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Kind {
	
	@EmbeddedId
	private TimedId id;

	private String kindAkten;
}
