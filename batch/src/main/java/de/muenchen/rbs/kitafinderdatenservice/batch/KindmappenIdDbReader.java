package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindmappeIdRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KindmappenIdDbReader implements ItemReader<KindmappeId> {

	private KindmappeIdRepository idRepository;

	private Iterator<KindmappeId> ids;

	@Setter
	private int currentIndex;

	private int batchSize = 50;

	public KindmappenIdDbReader(KindmappeIdRepository idRepository,
			@Value("${app.kitafinder.id-batch-size:100}") int batchSize) {
		this.idRepository = idRepository;
		this.batchSize = batchSize;
	}

	@Override
	public KindmappeId read() {
		try {
			return getNextId();
		} catch (NoSuchElementException | NullPointerException e) {
			// Aktueller Batch ist leer, lade nächsten Batch.
			this.loadNextBatch();
			try {
				return getNextId();
			} catch (NoSuchElementException | NullPointerException e2) {
				// Am Ende wird null zurückgegeben
				return null;
			}
		}
	}

	private KindmappeId getNextId() {
		KindmappeId nextId = ids.next();
		currentIndex++;
		return nextId;
	}

	private void loadNextBatch() {
		Page<KindmappeId> loadedIds = idRepository
				.findAll(Pageable.ofSize(batchSize).withPage(currentIndex / batchSize));
		// skip to the correct position if necessary
		ids = loadedIds.stream().skip(currentIndex % batchSize).iterator();
	}

}
