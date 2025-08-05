package de.muenchen.rbs.kitafinderdatenservice.repository;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.muenchen.rbs.kitafinderdatenservice.domain.events.OutboxState;
import de.muenchen.rbs.kitafinderdatenservice.domain.events.Outboxevent;

public interface OutboxEventRepository
		extends PagingAndSortingRepository<Outboxevent, UUID>, CrudRepository<Outboxevent, UUID> {

	List<Outboxevent> findByAggregateId(Long aggregateId);

	/**
	 * Sucht alle Events im angegeben Status (sortiert nach Timestamp, älteste
	 * Events zuerst).
	 * 
	 * @param states Suchkriterium {@link OutboxState}s
	 * @return einen {@link Stream} der gefundenen Events
	 */
	@Query(value = "SELECT event FROM #{#entityName} event " + "WHERE event.state IN :states "
			+ "AND (event.retryNotBefore IS NULL OR event.retryNotBefore <= :currentTime )"
			+ "ORDER BY event.timestamp")
	Stream<Outboxevent> findAllByStateOrderByTimestamp(EnumSet<OutboxState> states, LocalDateTime currentTime);

	/**
	 * Liefert den aktuellsten Timestamp.
	 * 
	 * @param states Alle {@link OutboxState} nach denen gesucht werden soll. Darf
	 *               nicht <null> sein.
	 * @param type   Nullable Typ des Events, nach dem gesucht werden soll.
	 * @return den aktuellsten Timestamp der gefundenen Events
	 */
	@Query(value = "SELECT MAX(timestamp) FROM #{#entityName} event " + "WHERE (event.state IN :states) "
			+ "AND (:type IS NULL OR event.type = :type)")
	LocalDateTime findMostRecentTimestamp(EnumSet<OutboxState> states, String type);

}
