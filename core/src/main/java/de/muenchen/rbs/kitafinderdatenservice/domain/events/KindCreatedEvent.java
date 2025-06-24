package de.muenchen.rbs.kitafinderdatenservice.domain.events;

import java.time.LocalDateTime;

import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("KIND_CREATED")
public class KindCreatedEvent extends KinddatenEvent {

	@ManyToOne
	@JoinColumn(name = "kindId", referencedColumnName = "id", insertable = false, updatable = false)
	@JoinColumn(name = "exportId", referencedColumnName = "exportId", insertable = false, updatable = false)
	private Kind kind;

	private LocalDateTime timestamp;

}
