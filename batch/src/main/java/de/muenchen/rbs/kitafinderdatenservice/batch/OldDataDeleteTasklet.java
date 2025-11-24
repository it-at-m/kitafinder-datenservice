package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportRun;
import de.muenchen.rbs.kitafinderdatenservice.domain.ExportStatus;
import de.muenchen.rbs.kitafinderdatenservice.repository.ExportRunRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OldDataDeleteTasklet implements Tasklet {

	private final KindRepository kindRepository;
	private final ExportRunRepository exportRunRepository;

	private final int cleanupKeepAge;
	private final int cleanupKeepNumber;

	public OldDataDeleteTasklet(KindRepository kindRepository, ExportRunRepository exportRunRepository,
			@Value("${app.kitafinder.cleanup-keep-age:2}") int cleanupKeepAge,
			@Value("${app.kitafinder.cleanup-keep-number:2}") int cleanupKeepNumber) {
		super();
		this.kindRepository = kindRepository;
		this.exportRunRepository = exportRunRepository;
		this.cleanupKeepAge = cleanupKeepAge;

		if (cleanupKeepNumber < 1) {
			log.warn(
					"Ignoring app.kitafinder.cleanup-keep-number because it is smaller than 1. Keeping 1 previous run to be able to generate events.");
			this.cleanupKeepNumber = 1;
		} else {
			this.cleanupKeepNumber = cleanupKeepNumber;
		}
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// Clean up anything older than the desired age.
		// Add a few hours to account for small differences in start time or duration.
		// This means any rows whose age in days is greater <cleanupAge> days gets
		// deleted.
		// i.e. cleanupAge = 2 -> data from yesterday (age = 1 day) will stay, while
		// data from any day before will be deleted.
		LocalDateTime ageThreshold = LocalDateTime.now().minusDays(cleanupKeepAge).plusHours(3);
		log.info("Starting Kitafinder cleanup. Keeping {} runs or runs newer than age {}...", cleanupKeepNumber,
				cleanupKeepAge);

		List<ExportRun> runs = exportRunRepository.findAllSuccessfullOrdered();

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

		for (ExportRun run : runsToDelete) {
			deleteDataForExportRun(run);
		}

		// delete data for all stuck or failed runs (keep only the current run)
		JobParameters parameters = contribution.getStepExecution().getJobParameters();
		long exportRunId = parameters.getLong("EXPORT_ID");
		List<ExportRun> stuckRuns = exportRunRepository.findAllRunningOrFailed().stream()
				.filter(r -> r.getId() != exportRunId).toList();

		for (ExportRun run : stuckRuns) {
			deleteDataForExportRun(run);
		}

		return RepeatStatus.FINISHED;
	}

	private void deleteDataForExportRun(ExportRun run) {
		log.info("Delete data for {}", run.toString());

		kindRepository.deleteByExportId(run.getId());

		run.setStatus(ExportStatus.DELETED);
		exportRunRepository.save(run);
	}
}
