package de.muenchen.rbs.kitafinderdatenservice.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.repository.KindmappeIdRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class KindmappenIdDeleteTasklet implements Tasklet {

	private final KindmappeIdRepository repository;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		repository.deleteAll();
		return RepeatStatus.FINISHED;
	}
}
