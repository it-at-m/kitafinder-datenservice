package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportRun;
import de.muenchen.rbs.kitafinderdatenservice.domain.ExportStatus;
import de.muenchen.rbs.kitafinderdatenservice.repository.ExportRunRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JobCompletionListener implements JobExecutionListener {

	@Autowired
	private ExportRunRepository exportRunRepository;
	
	@Autowired
	private KindRepository kindRepository;

	@Autowired
	ThreadPoolTaskExecutor executor;
	
	@Value("${app.ignoreeventsforsuccess:false}")
	private boolean ignoreEventsForSuccess;
	
	@Value("${app.kind-difference-threshold:20000}")
	private int kindDifferenceThreshold;

	@Override
	public void afterJob(JobExecution jobExecution) {
		this.shutdownExecutor();

		JobParameters parameters = jobExecution.getJobParameters();
		long exportRunId = parameters.getLong("EXPORT_ID");

		ExportRun exportRun = exportRunRepository.findById(exportRunId).orElseThrow(
				() -> new IllegalStateException("Trying to restart a previous ExportRun that cannot be found."));

		if (ExitStatus.COMPLETED.equals(jobExecution.getExitStatus()) && jobExecution.getStepExecutions().stream()
				// if ignore is set filter out the eventgenerationdecider step
				.filter(e -> !ignoreEventsForSuccess || !"eventGenerationDeciderStep".equals(e.getStepName()))
				// make sure no run is failed
				.noneMatch(e -> e.getExitStatus() == ExitStatus.FAILED)) {
			
			long currentKindNumber = kindRepository.findByExportId(exportRunId, Pageable.ofSize(1)).getTotalElements();
			long previousKindNumber = kindRepository.findAllAktuell(Pageable.ofSize(1)).getTotalElements();
			
			if (Math.abs(currentKindNumber - previousKindNumber) > kindDifferenceThreshold) {
				log.error("Kind number changed more than expected. It looks like something unexpected went wrong. Setting run to error...");
				exportRun.setStatus(ExportStatus.ERROR);
			} else {
				log.info("All relevant steps executed successfully. Setting Job Status to SUCCESS.");
				exportRun.setStatus(ExportStatus.SUCCESS);
			}
		} else {
			log.warn("Detected one or more FAILED steps. Setting Job Status to ERROR.");
			jobExecution.getStepExecutions().stream().forEachOrdered(step -> {
				log.info("Step {} has status {}", step.getStepName(), step.getExitStatus().getExitCode());
			});
			exportRun.setStatus(ExportStatus.ERROR);
		}
		exportRun.setEndTime(LocalDateTime.now());
		exportRunRepository.save(exportRun);
	}

	private void shutdownExecutor() {
		executor.shutdown();
		try {
			if (!executor.getThreadPoolExecutor().awaitTermination(60, TimeUnit.SECONDS)) {
				executor.getThreadPoolExecutor().shutdownNow();
			}
		} catch (InterruptedException ex) {
			executor.getThreadPoolExecutor().shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}
