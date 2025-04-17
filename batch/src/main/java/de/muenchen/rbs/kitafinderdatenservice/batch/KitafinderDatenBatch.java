package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;
import de.muenchen.rbs.kitafinderdatenservice.domain.mapper.KindMapper;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportService;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KitafinderExport;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindmappeIdRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KitafinderDatenBatch {

	private KitafinderExportService service;

	private KindmappeIdRepository idRepository;
	private KindRepository repository;

	private KindMapper mapper = KindMapper.INSTANCE;

	private final int batchSize;

	public KitafinderDatenBatch(KitafinderExportService service, KindRepository repository,
			KindmappeIdRepository idRepository, @Value("${app.kitafinder.data-batch-size:10}") int batchSize) {
		this.service = service;
		this.repository = repository;
		this.idRepository = idRepository;

		this.batchSize = batchSize;
	}

	public void loadKitafinderData() {
		LocalDateTime exportStart = LocalDateTime.now();

		log.info("Starting Kitafinder data export with batch-size {}...", batchSize);
		long numberOfIds = idRepository.count();

		Pageable page = Pageable.ofSize(batchSize).first();

		for (int i = 0; i < numberOfIds; i += batchSize) {
			List<KindmappeId> ids = idRepository.findAll(page).getContent();

			KitafinderExport data = service.loadKitafinderData(ids.stream().map(kmid -> kmid.getId()).toList());

			List<Kind> mappedData = data.getKindMappen().stream().map(kindmappe -> mapper.kindmappeToKind(kindmappe))
					.toList();

			repository.saveAll(mappedData);

			// Next page for next request
			page.next();
		}

		Duration dauer = Duration.between(exportStart, LocalDateTime.now());
		log.info("Kitafinder data export completed. Duration: {}, number of rows: {}", dauer.toString(), numberOfIds);
	}

}
