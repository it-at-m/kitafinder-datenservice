package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.text.DecimalFormat;
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
import de.muenchen.rbs.kitafinderdatenservice.domain.events.Outboxevent;
import de.muenchen.rbs.kitafinderdatenservice.domain.mapper.ExportErrorMapper;
import de.muenchen.rbs.kitafinderdatenservice.domain.mapper.KindMapper;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportService;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KitafinderExportDTO;
import de.muenchen.rbs.kitafinderdatenservice.repository.ExportErrorRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindmappeIdRepository;
import de.muenchen.rbs.kitafinderdatenservice.service.OutboxeventService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KitafinderDatenBatch {

	private KitafinderExportService service;

	private KindmappeIdRepository idRepository;
	private KindRepository repository;
	private ExportErrorRepository errorRepository;
	private OutboxeventService outboxeventService;

	private KindMapper mapper = KindMapper.INSTANCE;
	private ExportErrorMapper errorMapper = ExportErrorMapper.INSTANCE;

	private final int batchSize;
	private final int logIntervalPages;
	private static final DecimalFormat percentFormat = new DecimalFormat("##.##%");

	public KitafinderDatenBatch(KitafinderExportService service, KindRepository repository,
			ExportErrorRepository errorRepository, KindmappeIdRepository idRepository,
			OutboxeventService outboxeventService, @Value("${app.kitafinder.data-batch-size:10}") int batchSize,
			@Value("${app.log-interval-pages:10}") int logIntervalPages) {
		this.service = service;
		this.repository = repository;
		this.errorRepository = errorRepository;
		this.idRepository = idRepository;
		this.outboxeventService = outboxeventService;

		this.batchSize = batchSize;
		this.logIntervalPages = logIntervalPages;
	}

	public void loadKitafinderData(Integer exportRunId) {
		LocalDateTime exportStart = LocalDateTime.now();

		log.info("Starting Kitafinder data export with batch-size {}...", batchSize);
		long numberOfIds = idRepository.count();

		int successCount = 0;
		int errorCount = 0;
		int eventCount = 0;

		// Start loading data in batches
		for (Pageable page = Pageable.ofSize(batchSize).first(); page.getOffset() < numberOfIds; page.next()) {
			// Load Ids for this batch
			List<KindmappeId> ids = idRepository.findAll(page).getContent();

			// general errors
			List<ExportError> errors = new ArrayList<>();
			try {
				// parse errors
				List<ExportError> parseErrors = new ArrayList<>();
				// Load kitafinder data
				KitafinderExportDTO data = service.loadKitafinderData(ids.stream().map(kmid -> kmid.getId()).toList());
				List<Kind> mappedData = data.getKindMappen().stream().map(kindmappe -> {
					try {
						return mapper.kindmappeToKind(kindmappe, exportRunId);
					} catch (Exception e) {
						log.error("Error on mapping kitafinder kindmappe.");
						e.printStackTrace();

						ExportError error = errorMapper.kindmappeToExportError(kindmappe, exportRunId, e.getMessage());
						error.setErrorMessage(e.getMessage());

						parseErrors.add(error);
						return null;
					}
				}).filter(k -> k != null).toList();

				successCount += mappedData.size();
				repository.saveAll(mappedData);

				errors.addAll(parseErrors);

				// generate events
				List<Outboxevent> events = new ArrayList<>();
				for (Kind kind : mappedData) {
					events.addAll(this.createEvents(kind));
				}
				eventCount += events.size();
				outboxeventService.saveAll(events);

				if (page.getPageNumber() % logIntervalPages == logIntervalPages - 1) {
					log.info("Exported kindmappen {} to {} of {}. {}...", page.getOffset(),
							page.getOffset() + batchSize - 1, numberOfIds,
							percentFormat.format(page.getOffset() * 1.0f / numberOfIds));
				}
			} catch (Exception e) {
				log.error("Error on loading/saving kitafinder kindmappen page {}.", page.getPageNumber());
				e.printStackTrace();

				// skip full page
				errors = ids.stream().map(id -> errorMapper.idToExportError(id, exportRunId, e.getMessage())).toList();
			}

			errorCount += errors.size();
			errorRepository.saveAll(errors);
			// Next page for next request
			page = page.next();
		}

		Duration duration = Duration.between(exportStart, LocalDateTime.now());
		log.info(
				"Kitafinder data export completed. Duration: {}, number of rows: {}, number of errors: {}, number of events: {}",
				duration.toString(), successCount, errorCount, eventCount);
	}

	private List<Outboxevent> createEvents(Kind newKind) {
		List<Outboxevent> events = new ArrayList<>();

		// TODO: generate events
		// TODO: consider batching the retrieval of old data
		Optional<Kind> oldKind = repository.findMostRecentById(newKind.getId());

		if (oldKind.isEmpty()) {
			events.add(outboxeventService.buildKindCreated(newKind));
		} else {
			newKind.getVertraege().stream().filter(
					newV -> oldKind.get().getVertraege().stream().noneMatch(old -> old.getId().equals(newV.getId())))
					.forEach(newV -> events.add(outboxeventService.buildNewVertrag(newV)));
		}

		return events;
	}

}
