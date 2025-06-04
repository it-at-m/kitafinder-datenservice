package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.time.LocalDateTime;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Kind {

	@EmbeddedId
	private ExportId id;

	private LocalDateTime timestamp;
	
	private String vorname;
	private String nachname;
	private String geburtsdatum;

	private String kindAkten;

	@ManyToOne
	@JoinColumn(name = "exportId", nullable = false, insertable = false, updatable = false)
	private ExportRun exportRun;

}
