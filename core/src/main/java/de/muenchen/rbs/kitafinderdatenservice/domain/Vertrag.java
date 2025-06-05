package de.muenchen.rbs.kitafinderdatenservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.Kindakte;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
public class Vertrag {

	@JsonIgnore
	@EmbeddedId
	private ExportId id;

	@ManyToOne
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@JoinColumns({ @JoinColumn(name = "kindId", referencedColumnName = "id"),
			@JoinColumn(name = "kindExportId", referencedColumnName = "exportId") })
	private Kind kind;

	@JsonUnwrapped
	@Embedded
	private Kindakte daten;

	public Vertrag(Kind reference, Kindakte daten) {
		super();
		this.id = new ExportId(daten.getId(), reference.getId().getExportId());
		this.kind = reference;
		this.daten = daten;
	}

}