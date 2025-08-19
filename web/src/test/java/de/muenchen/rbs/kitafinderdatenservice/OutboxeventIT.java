package de.muenchen.rbs.kitafinderdatenservice;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.service.OutboxeventService;

@ActiveProfiles("test")
@SpringBootTest
public class OutboxeventIT {

	@Autowired
	private OutboxeventService service;

	@Test
	public void saveBasicEvent() {
		Kind kind = new Kind();
		kind.setId(1l);

		service.saveAll(List.of(service.buildKindCreated(kind)));
	}
}
