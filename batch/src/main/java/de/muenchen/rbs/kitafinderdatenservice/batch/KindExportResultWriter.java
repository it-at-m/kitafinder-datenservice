package de.muenchen.rbs.kitafinderdatenservice.batch;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportError;
import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindExportResult;
import de.muenchen.rbs.kitafinderdatenservice.domain.mapper.ExportErrorMapper;
import de.muenchen.rbs.kitafinderdatenservice.repository.ExportErrorRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KindExportResultWriter implements ItemWriter<KindExportResult> {

	private final KindRepository kindRepository;
	private final ExportErrorRepository errorRepository;
	private ExportErrorMapper errorMapper = ExportErrorMapper.INSTANCE;
	
	@Override
	public void write(Chunk<? extends KindExportResult> results) throws Exception {
		for (KindExportResult result : results) {
			if (result instanceof Kind kind) {
				try {
					kindRepository.save(kind);
				} catch (Exception e) {
					log.warn("Error [{}] on saving Kind with id {}. Saving error...", e.getMessage(), kind.getId());
					ExportError error = errorMapper.kindToExportError(kind, e.getMessage());
					errorRepository.save(error);
				}
			} else if (result instanceof ExportError error) {
				errorRepository.save(error);
			} else {
				throw new IllegalArgumentException(
						"Unknown type cannot be handled by KindExportResultWriter: " + result.getClass().getName());
			}
		}
	}

}
