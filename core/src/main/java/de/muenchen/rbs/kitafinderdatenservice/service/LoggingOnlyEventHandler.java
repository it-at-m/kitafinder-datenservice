package de.muenchen.rbs.kitafinderdatenservice.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import de.muenchen.rbs.kitafinderdatenservice.domain.events.Outboxevent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("logging-only-events")
public class LoggingOnlyEventHandler implements EventHandlerDelegate {

	public LoggingOnlyEventHandler() {
		log.info("Creating LoggingOnlyEventHandler.");
	}

	@Override
	public boolean supports(Outboxevent arg0) {
		return true;
	}

	@Override
	public void consumeEvent(Outboxevent event) {
		log.info(event.toString());
	}

}
