package de.muenchen.rbs.kitafinderdatenservice.batch.old;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportRun;
import de.muenchen.rbs.kitafinderdatenservice.domain.ExportStatus;
import de.muenchen.rbs.kitafinderdatenservice.repository.BewerbungRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.ExportRunRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.VertragRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Component
public class KitafinderCleanupBatch {

	private KindRepository kindRepository;
	private BewerbungRepository bewerbungRepository;
	private VertragRepository vertragRepository;
	private ExportRunRepository runRepository;

	private final int cleanupKeepAge;
	private final int cleanupKeepNumber;

	public KitafinderCleanupBatch(KindRepository repository, BewerbungRepository bewerbungRepository,
			VertragRepository VertragRepository, ExportRunRepository runRepository,
			@Value("${app.kitafinder.cleanup-keep-age:2}") int cleanupKeepAge,
			@Value("${app.kitafinder.cleanup-keep-number:2}") int cleanupKeepNumber) {
		this.kindRepository = repository;
		this.bewerbungRepository = bewerbungRepository;
		this.vertragRepository = VertragRepository;
		this.runRepository = runRepository;

		if (cleanupKeepNumber < 1) {
			log.warn(
					"Ignoring app.kitafinder.cleanup-keep-number because it is smaller than 1. Keeping 1 previous run to be able to generate events.");
			this.cleanupKeepNumber = 1;
		} else {
			this.cleanupKeepNumber = cleanupKeepNumber;
		}

		this.cleanupKeepAge = cleanupKeepAge;
	}

	public void cleanupOldRows() {
		LocalDateTime exportStart = LocalDateTime.now();

		// Clean up anything older than the desired age.
		// Add a few hours to account for small differences in start time or duration.
		// This means any rows whose age in days is greater <cleanupAge> days gets
		// deleted.
		// i.e. cleanupAge = 2 -> data from yesterday (age = 1 day) will stay, while
		// data from any day before will be deleted.
		LocalDateTime ageThreshold = LocalDateTime.now().minusDays(cleanupKeepAge).plusHours(3);
		log.info("Starting Kitafinder cleanup. Keeping {} runs or runs newer than age {}...", cleanupKeepNumber,
				cleanupKeepAge);

		List<ExportRun> runs = runRepository.findAllSuccessfullOrdered();

		int deletedRows = 0;
		List<ExportRun> runsToKeep = new ArrayList<>();
		List<ExportRun> runsToDelete = new ArrayList<>();
		if (runs.size() > cleanupKeepNumber) {
			// all recent runs will be kept
			for (ExportRun run : runs) {
				if (run.getStartTime().isAfter(ageThreshold) || runsToKeep.size() < cleanupKeepNumber) {
					runsToKeep.add(run);
				} else {
					runsToDelete.add(run);
				}
			}
		}

		log.info("Keeping runs {}", runsToKeep);
		if (runsToDelete.size() > 0) {
			log.info("Deleting runs {}", runsToDelete);

			for (ExportRun run : runsToDelete) {
				// delete directly to avoid problems with InheritanceType.JOINED and
				// Cascade-deleting
				bewerbungRepository.deleteByExportId(run.getId());
				vertragRepository.deleteByExportId(run.getId());
				deletedRows += kindRepository.deleteByExportId(run.getId());

				run.setStatus(ExportStatus.DELETED);
				runRepository.save(run);
			}
		} else {
			log.info("No runs to delete.");
		}

		Duration duration = Duration.between(exportStart, LocalDateTime.now());
		log.info("Kitafinder cleanup completed. Duration: {}, number of deleted rows: {}", duration.toString(),
				deletedRows);
	}

}
