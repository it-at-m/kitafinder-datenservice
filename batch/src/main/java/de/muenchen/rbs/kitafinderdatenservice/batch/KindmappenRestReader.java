package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportService;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KindmappeDTO;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KitafinderExportDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KindmappenRestReader implements ItemStreamReader<KindmappeDTO>, ItemStream {

	private KitafinderExportService service;
	private KindmappenIdDbReader idReader;

	private Iterator<KindmappeDTO> kindmappen;
	private int currentIndex;
	private static final String CURRENT_INDEX = "current.index";

	private int batchSize = 50;

	public KindmappenRestReader(@Value("${app.kitafinder.data-batch-size:50}") int batchSize,
			KindmappenIdDbReader idReader, KitafinderExportService service) {
		this.batchSize = batchSize;
		this.idReader = idReader;
		this.service = service;
	}

	@Override
	public KindmappeDTO read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		try {
			return getNextValue();
		} catch (NoSuchElementException | NullPointerException e) {
			// Aktueller Batch ist leer, lade nächsten Batch.
			this.loadNextBatch();
			try {
				return getNextValue();
			} catch (NoSuchElementException | NullPointerException e2) {
				// Am Ende wird null zurückgegeben
				return null;
			}
		}
	}

	private KindmappeDTO getNextValue() {
		KindmappeDTO nextId = kindmappen.next();
		currentIndex++;
		return nextId;
	}

	private void loadNextBatch() {
		List<KindmappeId> currentBatchIds = new ArrayList<>();
		currentBatchIds.clear();
		for (int i = 0; i < batchSize; i++) {
			KindmappeId item = idReader.read();
			if (item == null) {
				break; // Stop if no more items are available
			}
			currentBatchIds.add(item);
		}

		KitafinderExportDTO result = service
				.loadKitafinderData(currentBatchIds.stream().map(KindmappeId::getId).toList());
		this.kindmappen = result.getKindMappen().iterator();
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		if (executionContext.containsKey(CURRENT_INDEX)) {
			idReader.setCurrentIndex(Long.valueOf(executionContext.getLong(CURRENT_INDEX)).intValue());
		}

		log.info("Starting KindmappenRestReader from index {}.", currentIndex);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.putLong(CURRENT_INDEX, Long.valueOf(currentIndex).longValue());
	}

	@Override
	public void close() throws ItemStreamException {
	}

}
