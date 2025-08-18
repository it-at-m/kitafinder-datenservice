package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KindmappenIdRestReader extends AsyncQueueReader<KindmappeId>
		implements ItemStreamReader<KindmappeId>, ItemStream {

	private KitafinderExportService service;
	private int currentIndex;
	private int batchSize = 50;

	public KindmappenIdRestReader(KitafinderExportService service,
			@Value("${app.kitafinder.id-batch-size:100}") int batchSize, @Autowired TaskExecutor taskExecutor) {
		super(batchSize * 4, taskExecutor);
		this.service = service;
		this.batchSize = batchSize;
	}

	@Override
	protected List<KindmappeId> getNextBatch() {
		Collection<Long> loadedIds = service.loadKitafinderKindmappenIds(batchSize, currentIndex);
		this.currentIndex += loadedIds.size();
		List<KindmappeId> mappedIds = new ArrayList<>(loadedIds.stream().map(id -> new KindmappeId(id)).toList());

		// mark stream as done when result is last page
		if (loadedIds.size() < batchSize) {
			mappedIds.add(null);
		}

		return mappedIds;
	}
}
