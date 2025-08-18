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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KindmappenIdDbReader implements ItemReader<KindmappeId> {

	private KindmappeIdRepository idRepository;

	private Iterator<KindmappeId> ids;

	private int currentIndex;

	private int batchSize;

	private boolean done = false;

	public KindmappenIdDbReader(KindmappeIdRepository idRepository,
			@Value("${app.kitafinder.id-batch-size:100}") int batchSize) {
		this.idRepository = idRepository;
		this.batchSize = batchSize;

		loadNextBatch();
	}

	@Override
	public KindmappeId read() {
		if (done) {
			return null;
		}

		try {
			return getNextId();
		} catch (NoSuchElementException e) {
			// Aktueller Batch ist leer, lade nächsten Batch.
			this.loadNextBatch();
			try {
				return getNextId();
			} catch (NoSuchElementException e2) {
				done = true;
				// Am Ende wird null zurückgegeben
				return null;
			}
		}
	}

	private KindmappeId getNextId() {
		KindmappeId nextId = ids.next();
		return nextId;
	}

	private void loadNextBatch() {
		Page<KindmappeId> loadedIds = idRepository
				.findAll(Pageable.ofSize(batchSize).withPage(currentIndex / batchSize));
		ids = loadedIds.stream().iterator();

		currentIndex += loadedIds.getSize();
	}

}
