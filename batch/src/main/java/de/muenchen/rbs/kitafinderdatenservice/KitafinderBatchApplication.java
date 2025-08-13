package de.muenchen.rbs.kitafinderdatenservice;

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
	private Integer exportRunId;
	
	@Autowired
	private ExportRunRepository exportRunRepository;

	public static void main(String[] args) {
		SpringApplication.run(KitafinderBatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		JobParameters jobParameters;
		if (exportRunId != null) {
			// interpret first parameter as ID to use
			jobParameters = new JobParametersBuilder()
					.addLong("EXPORT_ID", Long.valueOf(exportRunId))
					.toJobParameters();		
		} else {
			jobParameters = new JobParametersBuilder()
					.addLong("EXPORT_ID", exportRunRepository.getNextId())
					.toJobParameters();			
		}

		jobLauncher.run(kitafinderJob, jobParameters);
	}
}