package de.muenchen.rbs.kitafinderdatenservice.domain.events;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
public class Outboxevent implements Serializable {

	private static final long serialVersionUID = 7587235234499648976L;

	@Id
	@Basic
	@GeneratedValue
	@ToString.Include
	private UUID id;

	@NotNull
	@ToString.Include
	@EqualsAndHashCode.Exclude
	private LocalDateTime timestamp;

	@NotNull
	@ToString.Include
	private long aggregateId;

	@NotNull
	@ToString.Include
	private String aggregateType;

	@NotNull
	@ToString.Include
	private EventType type;

	@NotNull
	@Enumerated(EnumType.STRING)
	private OutboxState state = OutboxState.PENDING;

	@JdbcTypeCode(SqlTypes.JSON)
	private JsonNode payload;

	@Lob
	private String errorMessage;

	private Integer retryCount;

	private LocalDateTime retryNotBefore;

}
