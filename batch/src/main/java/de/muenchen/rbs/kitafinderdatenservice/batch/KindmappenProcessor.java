package de.muenchen.rbs.kitafinderdatenservice.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.ExportError;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindExportResult;
import de.muenchen.rbs.kitafinderdatenservice.domain.mapper.ExportErrorMapper;
import de.muenchen.rbs.kitafinderdatenservice.domain.mapper.KindMapper;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KindmappeDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KindmappenProcessor implements ItemProcessor<KindmappeDTO, KindExportResult> {

	private int exportRunId;
	private KindMapper mapper;
	private ExportErrorMapper errorMapper;

	public KindmappenProcessor(int exportRunId, KindMapper mapper, ExportErrorMapper errorMapper) {
		super();
		this.exportRunId = exportRunId;
		this.mapper = mapper;
		this.errorMapper = errorMapper;
	}

	@Override
	public KindExportResult process(KindmappeDTO kindmappe) throws Exception {
		try {
			return mapper.kindmappeToKind(kindmappe, exportRunId);
		} catch (Exception e) {
			log.error("Error on mapping kitafinder kindmappe.");
			ExportError error = errorMapper.kindmappeToExportError(kindmappe, exportRunId, e.getMessage());
			error.setErrorMessage(e.getMessage());
			return error;
		}
	}

}
