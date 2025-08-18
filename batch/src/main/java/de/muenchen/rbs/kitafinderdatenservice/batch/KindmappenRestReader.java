package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportException;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportService;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KindmappeDTO;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KitafinderExportDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KindmappenRestReader extends AsyncQueueReader<KindmappeDTO> {

	private KitafinderExportService service;
	private KindmappenIdDbReader idReader;

	private int batchSize;

	public KindmappenRestReader(@Value("${app.kitafinder.data-batch-size:50}") int batchSize,
			@Autowired TaskExecutor taskExecutor, KindmappenIdDbReader idReader, KitafinderExportService service) {
		super(batchSize * 4, taskExecutor);
		this.batchSize = batchSize;
		this.idReader = idReader;
		this.service = service;
	}

	@Override
	protected List<KindmappeDTO> getNextBatch() {
		List<KindmappeId> currentBatchIds = new ArrayList<>();
		boolean markAsDone = false;
		for (int i = 0; i < batchSize; i++) {
			KindmappeId item = idReader.read();

			if (item == null) {
				markAsDone = true;
				break; // Stop if no more items are available
			}
			currentBatchIds.add(item);
		}

		List<KindmappeDTO> nextBatch = new ArrayList<>();
		
		if (currentBatchIds.size() > 0) {
			try {
				KitafinderExportDTO result = service
						.loadKitafinderData(currentBatchIds.stream().map(KindmappeId::getId).toList());
				nextBatch.addAll(result.getKindMappen());
			} catch (KitafinderExportException e) {
				log.error("Error on loading kitafinder data for ids {}.", currentBatchIds);
				nextBatch.addAll(currentBatchIds.stream()
						.map(id -> KindmappeDTO.builder().id(id.getId()).isGefunden(false).build()).toList());
			}
		} else {
			markAsDone = true;
		}

		if (markAsDone) {
			nextBatch.add(null);
		}

		return nextBatch;
	}

}
