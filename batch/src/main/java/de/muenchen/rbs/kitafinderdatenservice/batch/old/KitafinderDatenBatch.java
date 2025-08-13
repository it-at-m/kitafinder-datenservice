package de.muenchen.rbs.kitafinderdatenservice.batch.old;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.events.Outboxevent;
import de.muenchen.rbs.kitafinderdatenservice.domain.mapper.ExportErrorMapper;
import de.muenchen.rbs.kitafinderdatenservice.domain.mapper.KindMapper;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportService;
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
	private KindRepository kindRepository;
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
		this.kindRepository = repository;
		this.errorRepository = errorRepository;
		this.idRepository = idRepository;
		this.outboxeventService = outboxeventService;

		this.batchSize = batchSize;
		this.logIntervalPages = logIntervalPages;
	}

	public void loadKitafinderData(Integer exportRunId) {
		LocalDateTime exportStart = LocalDateTime.now();

		long numberOfIds = idRepository.count();
		log.info("Starting Kitafinder data export with batch-size {} for {} records...", batchSize, numberOfIds);

		// TODO
		int numberOfThreads = 7;

		List<KitafinderDatenImport> threads = new ArrayList<>();
		// Start loading data in batches
		for (int threadNumber = 0, currentPage = 0; threadNumber < numberOfThreads; threadNumber++) {
			long numberOfTotalPages = numberOfIds / batchSize;
			long minimumNumberOfPages = numberOfTotalPages / numberOfThreads;
			long numberOfRemainingPages = numberOfTotalPages - (numberOfThreads * minimumNumberOfPages);
			long numberOfPagesForThisThread = minimumNumberOfPages + (threadNumber < numberOfRemainingPages ? 1 : 0);

			log.info("Thread {}, total {}, this {}, totalRows: {}", threadNumber, numberOfTotalPages,
					numberOfPagesForThisThread, numberOfPagesForThisThread * batchSize);

			KitafinderDatenImport importThread = KitafinderDatenImport.builder().pageSize(batchSize)
					.numberOfPages((int) numberOfPagesForThisThread).startingPage(currentPage).exportRunId(exportRunId)
					.idRepository(idRepository).kindRepository(kindRepository).errorRepository(errorRepository)
					.service(service).outboxeventService(outboxeventService).mapper(mapper).errorMapper(errorMapper)
					.build();
			threads.add(importThread);

			currentPage += numberOfPagesForThisThread;
		}

		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

		int importCount = 0;
		int eventCount = 0;
		int errorCount = 0;

		try {
			List<Future<KitafinderDatenBatchResult>> results = executor.invokeAll(threads);
			for (Future<KitafinderDatenBatchResult> future : results) {
				try {
					importCount += future.get().getImportCount();
					eventCount += future.get().getEventCount();
					errorCount += future.get().getErrorCount();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace(); // Handle exceptions
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace(); // Handle exceptions
		} finally {
			executor.shutdown(); // Shutdown the executor
		}

		Duration duration = Duration.between(exportStart, LocalDateTime.now());
		log.info(
				"Kitafinder data export completed. Duration: {}, number of rows: {}, number of errors: {}, number of events: {}",
				duration.toString(), importCount, errorCount, eventCount);
	}

	private List<Outboxevent> createEvents(Kind newKind) {
		List<Outboxevent> events = new ArrayList<>();

		// TODO: generate events
		// TODO: consider batching the retrieval of old data
		Optional<Kind> oldKind = kindRepository.findMostRecentById(newKind.getId());

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
