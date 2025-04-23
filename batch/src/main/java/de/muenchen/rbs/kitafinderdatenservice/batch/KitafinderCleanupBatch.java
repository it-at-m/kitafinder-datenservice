package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Component
public class KitafinderCleanupBatch {

	private KindRepository repository;

	private final int cleanupAge;

	public KitafinderCleanupBatch(KindRepository repository, @Value("${app.kitafinder.cleanup-age:2}") int cleanupAge) {
		this.repository = repository;

		this.cleanupAge = cleanupAge;
	}

	public void cleanupOldRows() {
		LocalDateTime exportStart = LocalDateTime.now();

		// Clean up anything older than the desired age.
		// Add a few hours to account for small differences in start time or duration.
		// This means any rows whose age in days is greater <cleanupAge> days gets deleted.
		// i.e. cleanupAge = 2 -> data from yesterday (age = 1 day) will stay, while data from any day before will be deleted.
		LocalDateTime ageThreshold = LocalDateTime.now().minusDays(cleanupAge).plusHours(3);

		log.info("Starting Kitafinder cleanup with cleanup age {}...", cleanupAge);

		int deletedRows = repository.deleteOldRows(ageThreshold);

		Duration duration = Duration.between(exportStart, LocalDateTime.now());
		log.info("Kitafinder cleanup completed. Duration: {}, number of deleted rows: {}", duration.toString(),
				deletedRows);
	}

}
