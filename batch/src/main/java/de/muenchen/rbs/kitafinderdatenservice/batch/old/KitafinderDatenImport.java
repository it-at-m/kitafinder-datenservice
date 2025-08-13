package de.muenchen.rbs.kitafinderdatenservice.batch.old;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.springframework.data.domain.Pageable;

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
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
public class KitafinderDatenImport implements Callable<KitafinderDatenBatchResult> {

	private int startingPage;
	private int pageSize;
	private int numberOfPages;
	private int exportRunId;

	private KindmappeIdRepository idRepository;
	private KindRepository kindRepository;
	private ExportErrorRepository errorRepository;
	private KitafinderExportService service;
	private OutboxeventService outboxeventService;
	private KindMapper mapper;
	private ExportErrorMapper errorMapper;

	@Override
	public KitafinderDatenBatchResult call() throws Exception {
		log.info("Starting to load kitafinder data starting from page {}. Will load {} pages with {} rows.",
				startingPage, numberOfPages, pageSize);

		int successCount = 0;
		int errorCount = 0;
		int eventCount = 0;

		// Start loading data in batches
		for (Pageable page = Pageable.ofSize(pageSize).withPage(startingPage); page.getPageNumber() < startingPage
				+ numberOfPages; page.next()) {
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
				kindRepository.saveAll(mappedData);

				errors.addAll(parseErrors);

				// generate events
				List<Outboxevent> events = new ArrayList<>();
				for (Kind kind : mappedData) {
					events.addAll(this.createEvents(kind));
				}
				eventCount += events.size();
				outboxeventService.saveAll(events);
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

		log.info(
				"Finished loading {} pages of kitafinder data starting from page {}. Loaded {} rows, generated {} events and registered {} erroneous rows.",
				numberOfPages, startingPage, successCount, eventCount, errorCount);
		return new KitafinderDatenBatchResult(successCount, eventCount, errorCount);
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
