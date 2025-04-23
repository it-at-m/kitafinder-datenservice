package de.muenchen.rbs.kitafinderdatenservice;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

import de.muenchen.rbs.kitafinderdatenservice.batch.KitafinderCleanupBatch;
import de.muenchen.rbs.kitafinderdatenservice.batch.KitafinderDatenBatch;
import de.muenchen.rbs.kitafinderdatenservice.batch.KitafinderIdBatch;
import de.muenchen.rbs.kitafinderdatenservice.domain.ExportRun;
import de.muenchen.rbs.kitafinderdatenservice.domain.ExportStatus;
import de.muenchen.rbs.kitafinderdatenservice.repository.ExportRunRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableRetry
@SpringBootApplication
public class KitafinderBatchApplication implements CommandLineRunner {

	@Autowired
	private KitafinderIdBatch idBatch;
	@Autowired
	private KitafinderDatenBatch datenBatch;
	@Autowired
	private KitafinderCleanupBatch cleanupBatch;
	@Autowired
	private ExportRunRepository exportRunRepository;

	public static void main(String[] args) {
		SpringApplication.run(KitafinderBatchApplication.class, args);
	}

	@Override
	public void run(String... args) {
		ExportRun run = new ExportRun();
		run.setStatus(ExportStatus.RUNNING);
		run.setStartTime(LocalDateTime.now());
		run = exportRunRepository.save(run);

		log.info("Starting export {} at {}...", run.getId(), run.getStartTime());

		try {
			doExport(run.getId());
			run.setStatus(ExportStatus.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			run.setStatus(ExportStatus.ERROR);
		}

		run.setEndTime(LocalDateTime.now());
		run = exportRunRepository.save(run);

		Duration duration = Duration.between(run.getStartTime(), run.getEndTime());
		log.info("Finished export {} at {} with status {}. Took {}...", run.getId(), run.getEndTime(), run.getStatus(),
				duration.toString());

		System.exit(0);
	}

	@Transactional
	private void doExport(Integer exportRunId) {
		idBatch.loadKitafinderIds();

		datenBatch.loadKitafinderData(exportRunId);

		cleanupBatch.cleanupOldRows();
	}

}