package de.muenchen.rbs.kitafinderdatenservice.batch.old;

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

	private KindmappeIdRepository idRepository;

	private final int batchSize;
	private final int logIntervalPages;

	public KitafinderIdBatch(KitafinderExportService service, KindmappeIdRepository repository,
			@Value("${app.kitafinder.id-batch-size:100}") int batchSize,
			@Value("${app.log-interval-pages:10}") int logIntervalPages) {
		this.service = service;
		this.idRepository = repository;

		this.batchSize = batchSize;
		this.logIntervalPages = logIntervalPages;
	}

	public void loadKitafinderIds() {
		LocalDateTime exportStart = LocalDateTime.now();

		log.info("Starting Kitafinder id export with batch-size {}...", batchSize);
		log.info("Removing previous ids...");
		idRepository.deleteAll();
		int offset = 0;

		log.info("Loading ids...");
		// Lade Ids, bis eine Page nicht mehr voll ist.
		Collection<Integer> newIds = null;
		while (newIds == null || newIds.size() == batchSize) {
			log.debug("Requesting {} ids starting from {}...", batchSize, offset);
			newIds = service.loadKitafinderKindmappenIds(batchSize, offset);
			idRepository.saveAll(newIds.stream().map(id -> new KindmappeId(id)).toList());
			offset += batchSize;
			if (offset/batchSize % logIntervalPages == logIntervalPages-1) {
				log.info("Loaded {} kindmappen ids...", offset);
			}
		}

		Duration duration = Duration.between(exportStart, LocalDateTime.now());
		log.info("Kitafinder id export completed. Duration: {}, number of ids: {}", duration.toString(),
				offset - batchSize + newIds.size());
	}

}
