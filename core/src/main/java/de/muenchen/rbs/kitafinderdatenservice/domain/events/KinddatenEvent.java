package de.muenchen.rbs.kitafinderdatenservice.domain.events;

import java.time.LocalDateTime;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "EVENT")
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE")
public class KinddatenEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EVENT_ID_SEQUENCE_GENERATOR")
	@SequenceGenerator(name = "EVENT_ID_SEQUENCE_GENERATOR", sequenceName = "EVENT_ID_SEQUENCE", allocationSize = 1)
	private Integer id;
	private LocalDateTime timestamp;

}
