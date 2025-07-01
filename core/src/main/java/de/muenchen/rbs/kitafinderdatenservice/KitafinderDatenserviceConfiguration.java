package de.muenchen.rbs.kitafinderdatenservice;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.muenchen.rbs.kitafinderdatenservice.service.EventHandlerDelegate;

@Configuration
@EnableScheduling
@EnablePluginRegistries(value = { EventHandlerDelegate.class })
public class KitafinderDatenserviceConfiguration {

}
