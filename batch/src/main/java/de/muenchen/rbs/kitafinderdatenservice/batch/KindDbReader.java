package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KindDbReader extends AsyncQueueBatchReader<Kind> {

	private KindRepository kindRepository;
	private Pageable pageable;

	public KindDbReader(KindRepository kindRepository, TaskExecutor taskExecutor,
			@Value("${app.kitafinder.data-batch-size:100}") int batchSize) {
		super(batchSize * 4, taskExecutor);
		this.kindRepository = kindRepository;

		this.pageable = Pageable.ofSize(batchSize).first();
	}

	@Override
	protected List<Kind> getNextBatch() {
		Page<Kind> currentBatch = kindRepository.findAllMostRecent(pageable);
		pageable = pageable.next();

		List<Kind> results = new ArrayList<>(currentBatch.getContent());
		if (currentBatch.isLast()) {
			results.add(null);
		}
		return results;
	}

}
