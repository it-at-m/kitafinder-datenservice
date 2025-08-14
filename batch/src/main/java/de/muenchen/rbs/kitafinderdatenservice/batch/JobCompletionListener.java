package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.time.LocalDateTime;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportRun;
import de.muenchen.rbs.kitafinderdatenservice.domain.ExportStatus;
import de.muenchen.rbs.kitafinderdatenservice.repository.ExportRunRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JobCompletionListener implements JobExecutionListener {

	@Autowired
	private ExportRunRepository exportRunRepository;

//	@Autowired
//	private ConfigurableApplicationContext context;
//	
//	@Autowired
//	ThreadPoolTaskExecutor executor;

	@Override
	public void afterJob(JobExecution jobExecution) {
		JobParameters parameters = jobExecution.getJobParameters();
		long exportRunId = parameters.getLong("EXPORT_ID");

		ExportRun exportRun = exportRunRepository.findById(exportRunId).orElseThrow(
				() -> new IllegalStateException("Trying to restart a previous ExportRun that cannot be found."));

		if (ExitStatus.COMPLETED == jobExecution.getExitStatus()) {
			exportRun.setStatus(ExportStatus.SUCCESS);
		} else {
			exportRun.setStatus(ExportStatus.ERROR);
		}
		exportRun.setEndTime(LocalDateTime.now());
		exportRunRepository.save(exportRun);

//		executor.setAwaitTerminationSeconds(120);
//		executor.setWaitForTasksToCompleteOnShutdown(true);
//		executor.initiateShutdown();
//		
//		context.close();
	}
}
