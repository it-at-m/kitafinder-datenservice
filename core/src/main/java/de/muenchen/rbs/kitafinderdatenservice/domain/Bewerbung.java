package de.muenchen.rbs.kitafinderdatenservice.domain;

import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.Kindakte;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Bewerbung {

	@EmbeddedId
	private ExportId id;

	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "kindId", referencedColumnName = "id", insertable = false, updatable = false),
			@JoinColumn(name = "exportId", referencedColumnName = "exportId", insertable = false, updatable = false) })
	private Kind kind;

	@Embedded
	private Kindakte daten;

	public Bewerbung(Integer exportId, Kindakte daten) {
		super();
		this.id = new ExportId(daten.getId(), exportId);
		this.daten = daten;
	}

}
