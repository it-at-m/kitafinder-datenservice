package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportRun;
import de.muenchen.rbs.kitafinderdatenservice.repository.ExportRunRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventGenerationDeciderTasklet implements Tasklet {

	private final ExportRunRepository exportRunRepository;

	public EventGenerationDeciderTasklet(ExportRunRepository exportRunRepository) {
		super();
		this.exportRunRepository = exportRunRepository;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		List<ExportRun> runs = exportRunRepository.findAllSuccessfullOrdered();

		if (runs.size() >= 1) {
			return RepeatStatus.FINISHED;
		} else {
			throw new RuntimeException("Not enough previous runs to generate events.");
		}
	}
}
