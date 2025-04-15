package de.muenchen.rbs.kitafinderdatenservice.batch;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.mapper.KindMapper;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportService;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KitafinderExport;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KitafinderExportBatch {

	private KitafinderExportService service;

	private KindRepository repository;

	private KindMapper mapper = KindMapper.INSTANCE;

	/**
	 * Erzeugt eine neue Instanz.
	 * 
	 * @param baseUrl          Base-URL der Einrichtungsverwaltung API
	 * @param webClientBuilder ein {@link WebClient.Builder}
	 * @param timeoutSeconds   der Timeout für Anfragen in Sekunden
	 */
	public KitafinderExportBatch(KitafinderExportService service, KindRepository repository) {
		this.service = service;
		this.repository = repository;
	}

	public void loadKitafinderData() {
		log.info("Starte Kitafinder-Export...");

		LocalDateTime exportStart = LocalDateTime.now();

		log.info("Frage Kindmappen-Ids beim Kitafinder ab...");
		List<Integer> ids = service.loadKitafinderKindmappenIds();
		log.info("{} Kindmappen-Ids ermittelt. Frage Daten ab...", ids);

		for (int i = 0; i < ids.size(); i = i + 10) {
			List<Integer> currentlyLoadingIds = ids.subList(i, Math.min(ids.size(), i + 10) - 1);
			KitafinderExport data = service.loadKitafinderData(currentlyLoadingIds);

			List<Kind> mappedData = data.getKindMappen().stream().map(kindmappe -> mapper.kindmappeToKind(kindmappe))
					.toList();

			log.info("{} Kindmappen-Ids abgefragt.", currentlyLoadingIds);

			repository.saveAll(mappedData);
		}

		Duration dauer = Duration.between(exportStart, LocalDateTime.now());

		log.info("Kitafinder-Export erfolgreich abgeschlossen. Dauer: {}, Anzahl der Datensätze: {}", dauer.toString(),
				ids.size());
	}

}
