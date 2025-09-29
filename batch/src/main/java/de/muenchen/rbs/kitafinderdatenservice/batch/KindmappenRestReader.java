package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportException;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportService;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KindmappeDTO;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KitafinderExportDTO;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindmappeIdRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KindmappenRestReader extends AsyncQueueBatchReader<KindmappeDTO> {

	private KitafinderExportService service;
	private KindmappeIdRepository idRepository;
	private Pageable idRepoPage;

	private int batchSize;

	public KindmappenRestReader(@Value("${app.kitafinder.data-batch-size:50}") int batchSize, TaskExecutor taskExecutor,
			KindmappeIdRepository idRepository, KitafinderExportService service) {
		super(batchSize * 4, taskExecutor);
		this.batchSize = batchSize;
		this.service = service;
		this.idRepository = idRepository;
		this.idRepoPage = PageRequest.of(0, this.batchSize, Sort.by("id"));
	}

	@Override
	protected List<KindmappeDTO> getNextBatch() {
		Page<KindmappeId> currentBatchIds = idRepository.findAll(idRepoPage);
		idRepoPage = idRepoPage.next();
		boolean markAsDone = currentBatchIds.isLast();

		List<KindmappeDTO> nextBatch = new ArrayList<>();

		if (currentBatchIds.hasContent()) {
			try {
				KitafinderExportDTO result = service
						.loadKitafinderData(currentBatchIds.stream().map(KindmappeId::getId).toList());
				nextBatch.addAll(result.getKindMappen());
			} catch (KitafinderExportException e) {
				e.printStackTrace();
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
