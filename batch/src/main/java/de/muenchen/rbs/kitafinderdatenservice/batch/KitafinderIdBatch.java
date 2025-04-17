package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportService;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindmappeIdRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KitafinderIdBatch {

	private KitafinderExportService service;

	private KindmappeIdRepository repository;

	private final int batchSize;

	public KitafinderIdBatch(KitafinderExportService service, KindmappeIdRepository repository,
			@Value("${app.kitafinder.id-batch-size:100}") int batchSize) {
		this.service = service;
		this.repository = repository;

		this.batchSize = batchSize;
	}

	public void loadKitafinderIds() {
		LocalDateTime exportStart = LocalDateTime.now();

		log.info("Starting Kitafinder id export with batch-size {}...", batchSize);
		log.info("Removing previous ids...");
		repository.deleteAll();
		int offset = 0;

		// Lade Ids, bis eine Page nicht mehr voll ist.
		Collection<Integer> newIds = null;
		while (newIds == null || newIds.size() == batchSize) {
			log.debug("Requesting {} ids starting from {}...", batchSize, offset);
			newIds = service.loadKitafinderKindmappenIds(batchSize, offset);
			repository.saveAll(newIds.stream().map(id -> new KindmappeId(id)).toList());
			offset += batchSize;
		}

		Duration dauer = Duration.between(exportStart, LocalDateTime.now());
		log.info("Kitafinder id export completed. Duration: {}, number of ids: {}", dauer.toString(),
				offset - batchSize + newIds.size());
	}

}
