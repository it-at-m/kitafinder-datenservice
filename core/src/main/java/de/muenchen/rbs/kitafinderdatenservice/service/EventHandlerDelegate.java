package de.muenchen.rbs.kitafinderdatenservice.service;

import org.springframework.plugin.core.Plugin;

import de.muenchen.rbs.kitafinderdatenservice.domain.events.Outboxevent;

/**
 * Delegate, der Events aus der Outbox verarbeitet (und an externe System o.ä.
 * verschickt).
 * 
 * @author michael.prankl
 *
 */
public interface EventHandlerDelegate extends Plugin<Outboxevent> {

	/**
	 * Consume the given event.
	 * 
	 * @param event a {@link OutboxEvent}
	 * @throws EventHandlerException when consuming failed
	 */
	void consumeEvent(Outboxevent event);

}
