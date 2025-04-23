package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ExportId implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * domain id
	 */
	private Integer id;
	
	/**
	 * technical id referencing an export run
	 */
	private Integer exportId;
}
