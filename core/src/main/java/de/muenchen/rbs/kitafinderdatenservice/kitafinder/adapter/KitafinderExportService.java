package de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KitafinderExport;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KitafinderKindmappenIds;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KitafinderResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.kitafinder", name = {"base-url", "username", "password"})
public class KitafinderExportService {

	private final WebClient webClient;

	private final long timeoutSeconds;

	private final String baseUrl;

	private final String kitafinderApiUsername;

	private final String kitafinderApiPassword;

	/**
	 * Erzeugt eine neue Instanz.
	 * 
	 * @param baseUrl          Base-URL der Einrichtungsverwaltung API
	 * @param webClientBuilder ein {@link WebClient.Builder}
	 * @param timeoutSeconds   der Timeout für Anfragen in Sekunden
	 */
	public KitafinderExportService(@Value("${app.kitafinder.base-url}") String baseUrl,
			@Value("${app.kitafinder.timeout-seconds:30}") long timeoutSeconds, WebClient.Builder webClientBuilder,
			@Value("${app.kitafinder.username}") String kitafinderApiUsername,
			@Value("${app.kitafinder.password}") String kitafinderApiPassword) {
		this.baseUrl = baseUrl;
		this.timeoutSeconds = timeoutSeconds;
		this.kitafinderApiUsername = kitafinderApiUsername;
		this.kitafinderApiPassword = kitafinderApiPassword;

		log.info("Initializing KitafinderService with baseUrl={} and timeout={}s", baseUrl, timeoutSeconds);

		this.webClient = webClientBuilder.baseUrl(this.baseUrl).build();
	}

	public <T extends KitafinderResponse> T kitafinderGetRequest(Class<T> returnType, Function<UriBuilder, URI> uri)
			throws KitafinderExportException {
		try {
			ResponseEntity<T> response = this.webClient.get().uri(uri).accept(MediaType.APPLICATION_JSON)
					.headers(headers -> {
						headers.add("Authorization", "Basic " + HttpHeaders.encodeBasicAuth(kitafinderApiUsername,
								kitafinderApiPassword, Charset.defaultCharset()));
					}).retrieve().toEntity(returnType).block(Duration.ofSeconds(this.timeoutSeconds));

			if (!response.getStatusCode().equals(HttpStatusCode.valueOf(200))) {
				throw new KitafinderExportException(
						"Fehler beim Laden der Kitafinder-Daten. Response-Code: " + response.getStatusCode().value());
			} else if (response.getBody().getFehlermeldung() != null) {
				throw new KitafinderExportException("Fehler beim Laden der Kitafinder-Daten. Fehlermeldung: "
						+ response.getBody().getFehlermeldung());
			} else {
				return response.getBody();
			}
		} catch (WebClientRequestException | WebClientResponseException e) {
			log.error("Unerwarteter Fehler bei Abfrage vom Kitafinder.", e);
			throw new KitafinderExportException("Fehler beim Laden der Kitafinder-Daten.");
		}
	}

	public KitafinderExport loadKitafinderData(Collection<Integer> kindMappenIds) {
		log.debug("Frage Daten beim Kitafinder ab. Ids: {}", kindMappenIds.toString());

		return this.kitafinderGetRequest(KitafinderExport.class,
				uriBuilder -> uriBuilder.path("/rbs/kindmappen").queryParam("kindMappenIds", kindMappenIds).build());
	}

	public List<Integer> loadKitafinderKindmappenIds() {
		final int chunkSize = 100;
		int offset = 0;

		List<Integer> kindmappenIds = new ArrayList<>();

		Collection<Integer> newIds = null;
		while (newIds == null || newIds.size() == chunkSize) {
			final int finalOffset = offset;
			log.info("Frage {} Kindmappen-Ids ab {} ab...", chunkSize, finalOffset);
			KitafinderKindmappenIds ids = this.kitafinderGetRequest(KitafinderKindmappenIds.class,
					uriBuilder -> uriBuilder.path("/rbs/kindmappenids").queryParam("offset", finalOffset)
							.queryParam("fetch", chunkSize).build());

			newIds = ids.getKindMappenIds();
			kindmappenIds.addAll(newIds);
			offset++;
		}

		return kindmappenIds;
	}

}
