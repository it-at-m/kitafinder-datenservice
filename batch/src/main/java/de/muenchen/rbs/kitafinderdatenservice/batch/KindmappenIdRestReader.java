package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KindmappenIdRestReader implements ItemStreamReader<KindmappeId>, ItemStream {

	private KitafinderExportService service;

	private Iterator<Long> ids;
	private int currentIndex;
    private static final String CURRENT_INDEX = "current.index";
    
	private int batchSize = 50;

	public KindmappenIdRestReader(KitafinderExportService service, 
			@Value("${app.kitafinder.id-batch-size:100}") int batchSize) {
		this.service = service;
		this.batchSize = batchSize;
	}

	@Override
	public KindmappeId read() {
		try {
			KindmappeId nextId = new KindmappeId(ids.next());
			currentIndex++;
			return nextId;
		} catch (NoSuchElementException | NullPointerException e) {
			// Current batch is empty. Read next batch.
			// Abort on error, because we don't want to export an incomplete dataset.
			Collection<Long> loadedIds = service.loadKitafinderKindmappenIds(batchSize, currentIndex);
			ids = loadedIds.iterator();
			try {
				KindmappeId nextId = new KindmappeId(ids.next());
				currentIndex++;
				return nextId;
			} catch (NoSuchElementException | NullPointerException e2) {
				// Am Ende wird null zurückgegeben
				return null;
			}
		}
	}
	
	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey(CURRENT_INDEX)) {
            currentIndex = Long.valueOf(executionContext.getLong(CURRENT_INDEX)).intValue();
        }
        else {
            currentIndex = 0;
        }
        
        log.info("Starting KindmappenIdRestReader from index {}.", currentIndex);
    }

	@Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putLong(CURRENT_INDEX, Long.valueOf(currentIndex).longValue());
    }

	@Override
    public void close() throws ItemStreamException {}
}
