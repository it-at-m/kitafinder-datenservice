package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ExportId.class)
public class ExportError implements KindExportResult {

	@Id
	private Long id;
	@Id
	private Long exportId;

	private LocalDateTime timestamp;

	private String errorMessage;

}
