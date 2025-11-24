package de.muenchen.rbs.kitafinderdatenservice.service;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class WebClientMetricsLogger implements ExchangeFilterFunction {
	private static final String METRICS_WEBCLIENT_START_TIME = WebClientMetricsLogger.class.getName() + ".START_TIME";

	@Override
	public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
		return next.exchange(request).doOnEach((signal) -> {
			if (!signal.isOnComplete()) {
				Long startTime = signal.getContextView().get(METRICS_WEBCLIENT_START_TIME);
				long duration = System.currentTimeMillis() - startTime;
				log.info("Call to {} took {}ms", request.url(), duration);
			}
		}).contextWrite(ctx -> ctx.put(METRICS_WEBCLIENT_START_TIME, System.currentTimeMillis()));
	}
}
