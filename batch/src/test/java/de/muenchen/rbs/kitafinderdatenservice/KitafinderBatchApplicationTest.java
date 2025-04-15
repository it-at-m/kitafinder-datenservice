package de.muenchen.rbs.kitafinderdatenservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportService;

@ActiveProfiles("test")
@SpringBootTest
class KitafinderBatchApplicationTest {

	@MockitoBean
	private KitafinderExportService service;

	@Test
	void contextLoads() {
	}

}
