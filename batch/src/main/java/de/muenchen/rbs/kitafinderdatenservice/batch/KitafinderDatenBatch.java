package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportError;
import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;
import de.muenchen.rbs.kitafinderdatenservice.domain.mapper.ExportErrorMapper;
import de.muenchen.rbs.kitafinderdatenservice.domain.mapper.KindMapper;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportService;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KitafinderExport;
import de.muenchen.rbs.kitafinderdatenservice.repository.ExportErrorRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindmappeIdRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KitafinderDatenBatch {

	private KitafinderExportService service;

	private KindmappeIdRepository idRepository;
	private KindRepository repository;
	private ExportErrorRepository errorRepository;

	private KindMapper mapper = KindMapper.INSTANCE;
	private ExportErrorMapper errorMapper = ExportErrorMapper.INSTANCE;

	private final int batchSize;

	public KitafinderDatenBatch(KitafinderExportService service, KindRepository repository,
			ExportErrorRepository errorRepository, KindmappeIdRepository idRepository,
			@Value("${app.kitafinder.data-batch-size:10}") int batchSize) {
		this.service = service;
		this.repository = repository;
		this.errorRepository = errorRepository;
		this.idRepository = idRepository;

		this.batchSize = batchSize;
	}

	public void loadKitafinderData(Integer exportRunId) {
		LocalDateTime exportStart = LocalDateTime.now();

		log.info("Starting Kitafinder data export with batch-size {}...", batchSize);
		long numberOfIds = idRepository.count();

		Pageable page = Pageable.ofSize(batchSize).first();

		int successCount = 0;
		int errorCount = 0;

		// Start loading data in batches
		for (int i = 0; i < numberOfIds; i += batchSize) {
			// Load Ids for this batch
			List<KindmappeId> ids = idRepository.findAll(page).getContent();

			// Load kitafinder data
			KitafinderExport data = service.loadKitafinderData(ids.stream().map(kmid -> kmid.getId()).toList());

			// parse/create errors
			List<ExportError> nonParsable = new ArrayList<>();
			List<Kind> mappedData = data.getKindMappen().stream().map(kindmappe -> {
				try {
					return mapper.kindmappeToKind(kindmappe, exportRunId);
				} catch (Exception e) {
					log.error("Error on mapping kitafinder kindmappe.");
					e.printStackTrace();

					ExportError error = errorMapper.kindmappeToExportError(kindmappe, exportRunId);
					error.setErrorMessage(e.getMessage());

					nonParsable.add(error);
					return null;
				}
			}).filter(k -> k != null).toList();

			successCount += mappedData.size();
			errorCount += nonParsable.size();

			// generate events
			for (Kind kind : mappedData) {
				this.createEvents(kind);
			}

			repository.saveAll(mappedData);
			errorRepository.saveAll(nonParsable);

			// Next page for next request
			page = page.next();
		}

		Duration duration = Duration.between(exportStart, LocalDateTime.now());
		log.info("Kitafinder data export completed. Duration: {}, number of rows: {}, number of errors: {}",
				duration.toString(), successCount, errorCount);
	}

	private void createEvents(Kind newKind) {
		// TODO: generate events
		// TODO: consider batching the retrieval of old data
		Optional<Kind> oldKind = repository.findMostRecentById(newKind.getId().getId());

		if (oldKind.isEmpty()) {
			// TODO: kind created event
		}
	}

}
