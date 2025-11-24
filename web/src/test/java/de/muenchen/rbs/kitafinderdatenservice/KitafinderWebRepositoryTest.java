package de.muenchen.rbs.kitafinderdatenservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import de.muenchen.rbs.kitafinderdatenservice.domain.Bewerbung;
import de.muenchen.rbs.kitafinderdatenservice.domain.ExportRun;
import de.muenchen.rbs.kitafinderdatenservice.domain.ExportStatus;
import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.Sorgeberechtigter;
import de.muenchen.rbs.kitafinderdatenservice.repository.ExportRunRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;

@ActiveProfiles("test")
@DataJpaTest
class KitafinderWebRepositoryTest {

	@Autowired
	private ExportRunRepository exportRepository;
	@Autowired
	private KindRepository repository;

	@Test
	void canSaveKindWithDependantObjects() {
		// Kind mit allen abhängigen Objekten anlegen
		ExportRun run = new ExportRun();
		run.setStatus(ExportStatus.SUCCESS);
		exportRepository.save(run);

		Kind k = new Kind();
		k.setId(1000l);
		k.setExportId(run.getId());

		Bewerbung b = new Bewerbung();
		b.setId(1000000l);
		b.setExportId(run.getId());

		Sorgeberechtigter sb = new Sorgeberechtigter();
		sb.setId(2000l);
		sb.setExportId(run.getId());
		b.setSorgeberechtigter1(sb);

		k.setBewerbungen(List.of(b));
		b.setKind(k);

		repository.save(k);

		assertThat(repository.findById(k.getFullId())).isPresent();

		repository.delete(k);

		assertThat(repository.findById(k.getFullId())).isEmpty();
	}

}
