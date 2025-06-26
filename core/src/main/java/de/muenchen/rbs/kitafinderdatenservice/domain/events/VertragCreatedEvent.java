package de.muenchen.rbs.kitafinderdatenservice.domain.events;

import de.muenchen.rbs.kitafinderdatenservice.domain.Vertrag;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("VERTRAG_CREATED")
public class VertragCreatedEvent extends KinddatenEvent {

	@ManyToOne
	@JoinColumn(name = "vertragId", referencedColumnName = "id")
	@JoinColumn(name = "exportId", referencedColumnName = "EXPORT_ID")
	private Vertrag kind;

}
