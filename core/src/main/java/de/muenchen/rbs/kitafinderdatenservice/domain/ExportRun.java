package de.muenchen.rbs.kitafinderdatenservice.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents runs of this application.
 */
@Entity
@Data
@NoArgsConstructor
public class ExportRun {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EXPORT_ID_SEQUENCE_GENERATOR")
	@SequenceGenerator(name = "EXPORT_ID_SEQUENCE_GENERATOR", sequenceName = "EXPORT_ID_SEQUENCE", allocationSize = 1)
	private Integer id;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	private ExportStatus status;
}
