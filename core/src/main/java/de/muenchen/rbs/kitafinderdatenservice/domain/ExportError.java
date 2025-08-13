package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ExportError extends KindExportResult {

	private LocalDateTime timestamp;

	private String errorMessage;

	@ManyToOne
	@JoinColumn(name = "exportId", nullable = false, insertable = false, updatable = false)
	private ExportRun exportRun;
}
