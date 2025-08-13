package de.muenchen.rbs.kitafinderdatenservice.batch;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportError;
import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindExportResult;
import de.muenchen.rbs.kitafinderdatenservice.repository.ExportErrorRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;

@Component
public class KindExportResultWriter implements ItemWriter<KindExportResult> {

	private KindRepository kindRepository;
	private ExportErrorRepository errorRepository;

	public KindExportResultWriter(KindRepository kindRepository, ExportErrorRepository errorRepository) {
		this.kindRepository = kindRepository;
		this.errorRepository = errorRepository;
	}

	@Override
	public void write(Chunk<? extends KindExportResult> results) throws Exception {
		for (KindExportResult result : results) {
			if (result instanceof Kind kind) {
				kindRepository.save(kind);
			} else if (result instanceof ExportError error) {
				errorRepository.save(error);
			} else {
				throw new IllegalArgumentException(
						"Unknown type cannot be handled by KindExportResultWriter: " + result.getClass().getName());
			}
		}
	}

}
