package de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Collection;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KitafinderExport;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KitafinderKindmappenIds;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KitafinderResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "app.kitafinder", name = { "base-url", "username", "password" })
public class KitafinderExportService {

	private final WebClient webClient;

	private final long timeoutSeconds;

	private final int retryMaxAttempts;

	private final String baseUrl;

	private final String kitafinderApiUsername;

	private final String kitafinderApiPassword;

	/**
	 * Creates a new instance.
	 * 
	 * @param baseUrl               base-URL of the kitafinder-API
	 * @param webClientBuilder      {@link WebClient.Builder}
	 * @param timeoutSeconds        timeout in seconds
	 * @param kitafinderApiUsername username for accessing the kitafinder-API
	 * @param kitafinderApiPassword password for accessing the kitafinder-API
	 */
	public KitafinderExportService(@Value("${app.kitafinder.base-url}") String baseUrl,
			@Value("${app.kitafinder.timeout-seconds:30}") long timeoutSeconds,
			@Value("${app.kitafinder.retry-attempts:3}") int retryMaxAttempts, WebClient.Builder webClientBuilder,
			@Value("${app.kitafinder.username}") String kitafinderApiUsername,
			@Value("${app.kitafinder.password}") String kitafinderApiPassword) {
		this.baseUrl = baseUrl;
		this.timeoutSeconds = timeoutSeconds;
		this.retryMaxAttempts = retryMaxAttempts;
		this.kitafinderApiUsername = kitafinderApiUsername;
		this.kitafinderApiPassword = kitafinderApiPassword;

		log.info("Initializing KitafinderService with baseUrl={}, timeout={}s and maxAttempts={}", this.baseUrl,
				this.timeoutSeconds, this.retryMaxAttempts);

		this.webClient = webClientBuilder.baseUrl(this.baseUrl).build();
	}

	@Retryable(maxAttemptsExpression = "#{@retryMaxAttempts}", retryFor = { KitafinderExportException.class })
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
						"An error occured when calling kitafinder. Response code: " + response.getStatusCode().value());
			} else if (response.getBody().getFehlermeldung() != null) {
				throw new KitafinderExportException(response.getBody().getFehlermeldung());
			} else {
				return response.getBody();
			}
		} catch (WebClientRequestException | WebClientResponseException e) {
			log.error("An error occured when calling the kitafinder.", e);
			throw new KitafinderExportException(e.getClass().getName());
		}
	}

	public KitafinderExport loadKitafinderData(Collection<Integer> kindMappenIds) {
		return this.kitafinderGetRequest(KitafinderExport.class,
				uriBuilder -> uriBuilder.path("/rbs/kindmappen").queryParam("kindMappenIds", kindMappenIds).build());
	}

	public Collection<Integer> loadKitafinderKindmappenIds(int chunkSize, int offset) {
		KitafinderKindmappenIds ids = this.kitafinderGetRequest(KitafinderKindmappenIds.class, uriBuilder -> uriBuilder
				.path("/rbs/kindmappenids").queryParam("offset", offset).queryParam("fetch", chunkSize).build());

		return ids.getKindMappenIds();
	}

}
