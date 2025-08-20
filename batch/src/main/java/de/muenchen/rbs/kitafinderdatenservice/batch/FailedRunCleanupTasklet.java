package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.util.Optional;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportRun;
import de.muenchen.rbs.kitafinderdatenservice.domain.ExportStatus;
import de.muenchen.rbs.kitafinderdatenservice.repository.BewerbungRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.ExportRunRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.VertragRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class FailedRunCleanupTasklet implements Tasklet {

	private final KindRepository kindRepository;
	private final BewerbungRepository bewerbungRepository;
	private final VertragRepository vertragRepository;
	private final ExportRunRepository exportRunRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Long exportRunId = (Long) chunkContext.getStepContext().getJobParameters().get("EXPORT_ID");
		log.info("Cleaning up after failed import...");

		Optional<ExportRun> run = exportRunRepository.findById(exportRunId);
		if (run.isPresent()) {
			bewerbungRepository.deleteByExportId(run.get().getId());
			vertragRepository.deleteByExportId(run.get().getId());
			kindRepository.deleteByExportId(run.get().getId());

			run.get().setStatus(ExportStatus.ERROR);
			exportRunRepository.save(run.get());
		} else {
			log.warn("No run found. Nothing to clean up.");
		}

		return RepeatStatus.FINISHED;
	}
}
