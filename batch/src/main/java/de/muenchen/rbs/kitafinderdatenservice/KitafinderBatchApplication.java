package de.muenchen.rbs.kitafinderdatenservice;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
	@Qualifier("importJob")
	private Job importJob;
	
	@Autowired
	@Qualifier("eventJob")
	private Job eventJob;

	@Value("${app.eventsonly:false}")
	private boolean eventsOnly;

	@Autowired
	private ExportRunRepository exportRunRepository;

	public static void main(String[] args) {
		SpringApplication.run(KitafinderBatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		JobParameters jobParameters;
		ExportRun exportRun = new ExportRun();
		if (eventsOnly) {
			log.info("Generation events for previous run");

			// add timestamp to allow multiple tries
			jobParameters = new JobParametersBuilder().addLocalDateTime("START", LocalDateTime.now()).toJobParameters();
			jobLauncher.run(eventJob, jobParameters);
		} else {
			exportRun.setStartTime(LocalDateTime.now());
			exportRun.setStatus(ExportStatus.RUNNING);
			exportRunRepository.save(exportRun);
			log.info("Starting new run with id {}...", exportRun.getId());

			jobParameters = new JobParametersBuilder().addLong("EXPORT_ID", exportRun.getId()).toJobParameters();
			jobLauncher.run(importJob, jobParameters);
		}
	}
}