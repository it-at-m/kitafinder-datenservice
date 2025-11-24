package de.muenchen.rbs.kitafinderdatenservice;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportRun;
import de.muenchen.rbs.kitafinderdatenservice.domain.ExportStatus;
import de.muenchen.rbs.kitafinderdatenservice.repository.ExportRunRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableRetry
@SpringBootApplication
public class KitafinderBatchApplication implements CommandLineRunner {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job kitafinderJob;

	@Value("${app.export.id}")
	private Long exportRunId;

	@Autowired
	private ExportRunRepository exportRunRepository;

	public static void main(String[] args) {
		SpringApplication.run(KitafinderBatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		JobParameters jobParameters;
		ExportRun exportRun = new ExportRun();
		if (exportRunId != null) {
			throw new IllegalArgumentException("Restarting a previous run by providing an exportId is not yet supported.");
			/*
			exportRun = exportRunRepository.findById(exportRunId).orElseThrow(
					() -> new IllegalStateException("Trying to restart a previous ExportRun that cannot be found."));
			log.info("Restarting previous run with id {}...", exportRunId);

			// interpret first parameter as ID to use
			jobParameters = new JobParametersBuilder().addLong("EXPORT_ID", exportRunId).toJobParameters();
			*/
		} else {
			exportRun.setStartTime(LocalDateTime.now());
			exportRun.setStatus(ExportStatus.RUNNING);
			exportRunRepository.save(exportRun);
			log.info("Starting new run with id {}...", exportRun.getId());

			jobParameters = new JobParametersBuilder().addLong("EXPORT_ID", exportRun.getId()).toJobParameters();
		}

		jobLauncher.run(kitafinderJob, jobParameters);
	}
}