package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportRun;
import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.Vertrag;
import de.muenchen.rbs.kitafinderdatenservice.repository.ExportRunRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.VertragRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventGenerationDeciderTasklet implements Tasklet {

	private final ExportRunRepository exportRunRepository;
	private final KindRepository kindRepository;
	private final VertragRepository vertragRepository;
	
	@Value("${app.event-threshold:20000}")
	private int eventThreshold;

	public EventGenerationDeciderTasklet(ExportRunRepository exportRunRepository, KindRepository kindRepository, VertragRepository vertragRepository) {
		super();
		this.exportRunRepository = exportRunRepository;
		this.kindRepository = kindRepository;
		this.vertragRepository = vertragRepository;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		List<ExportRun> runs = exportRunRepository.findAllSuccessfullOrdered();

		if (runs.size() >= 1) {
			Page<Kind> newKind = kindRepository.findNew(Pageable.ofSize(1));
			Page<Vertrag> newVertrag = vertragRepository.findNew(Pageable.ofSize(1));
			log.info("Expecting {} new Kind events.", newKind.getTotalElements());
			log.info("Expecting {} new Vertrag events.", newVertrag.getTotalElements());
			if (newKind.getTotalElements() > eventThreshold || newVertrag.getTotalElements() > eventThreshold) {
				log.error("Detected more new events than logically feasible. It seems like something went wrong, so this run will not generate any events...");
				throw new RuntimeException("Not enough previous runs to generate events.");
			}

			Page<Kind> kindAktuell = kindRepository.findAllAktuell(Pageable.ofSize(1));
			kindAktuell.get().findAny().ifPresentOrElse(k -> {
				log.info("Comparison run is {}.", k.getExportId());
			}, () -> {
				log.warn("No run to compare to!");
			});

			return RepeatStatus.FINISHED;
		} else {
			throw new RuntimeException("Not enough previous runs to generate events.");
		}
	}
}
