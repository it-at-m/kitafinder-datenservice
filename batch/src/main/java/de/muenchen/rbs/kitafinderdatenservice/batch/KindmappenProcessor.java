package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.time.LocalDateTime;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportError;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindExportResult;
import de.muenchen.rbs.kitafinderdatenservice.domain.mapper.ExportErrorMapper;
import de.muenchen.rbs.kitafinderdatenservice.domain.mapper.KindMapper;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KindmappeDTO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KindmappenProcessor implements ItemProcessor<KindmappeDTO, KindExportResult> {

	@Setter
	private long exportRunId;

	private KindMapper mapper = KindMapper.INSTANCE;
	private ExportErrorMapper errorMapper = ExportErrorMapper.INSTANCE;

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		JobParameters parameters = stepExecution.getJobExecution().getJobParameters();
		this.exportRunId = parameters.getLong("EXPORT_ID");
	}

	@Override
	public KindExportResult process(KindmappeDTO kindmappe) throws Exception {
		if (kindmappe.isGefunden()) {
			try {
				return mapper.kindmappeToKind(kindmappe, exportRunId);
			} catch (Exception e) {
				log.error("Error on mapping kitafinder kindmappe.");
				ExportError error = errorMapper.kindmappeToExportError(kindmappe, exportRunId, e.getMessage());
				error.setErrorMessage(e.getMessage());
				return error;
			}
		} else {
			ExportError error = ExportError.builder().id(kindmappe.getId()).exportId(exportRunId)
					.errorMessage("Nicht gefunden").timestamp(LocalDateTime.now()).build();
			return error;
		}
	}

}
