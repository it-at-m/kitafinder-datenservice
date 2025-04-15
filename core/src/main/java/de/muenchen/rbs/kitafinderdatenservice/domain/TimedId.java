package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TimedId implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private LocalDateTime stand;
}
