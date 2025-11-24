package de.muenchen.rbs.kitafinderdatenservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import de.muenchen.rbs.kitafinderdatenservice.batch.KindmappenIdRestReader;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportService;

public class KindmappenIdRestReaderTest {

	private KitafinderExportService service = Mockito.mock(KitafinderExportService.class);

	private KindmappenIdRestReader sut;

	@BeforeEach
	private void setup() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(3);
		executor.setQueueCapacity(Integer.MAX_VALUE);
		executor.setThreadNamePrefix("batch-kitafinder-export-test-");
		executor.initialize();

		this.sut = new KindmappenIdRestReader(service, 5, executor);
	}

	@Test
	public void testReadSinglePage() throws Exception {
		List<Long> numbers = List.of(0l, 1l, 2l, 3l);
		List<KindmappeId> ids = numbers.stream().map(id -> new KindmappeId(id)).toList();
		Mockito.when(service.loadKitafinderKindmappenIds(Mockito.anyInt(), Mockito.anyInt())).thenReturn(numbers);

		sut.open(null);

		assertThat(sut.read()).isEqualTo(ids.get(0));
		assertThat(sut.read()).isEqualTo(ids.get(1));
		assertThat(sut.read()).isEqualTo(ids.get(2));
		assertThat(sut.read()).isEqualTo(ids.get(3));
		assertThat(sut.read()).isNull();

		Mockito.verify(service, times(1)).loadKitafinderKindmappenIds(Mockito.anyInt(), Mockito.anyInt());
	}
	@Test
	public void testReadMultiplePages() throws Exception {
		List<Long> numbers1 = List.of(0l, 1l, 2l, 3l, 4l);
		List<KindmappeId> ids1 = numbers1.stream().map(id -> new KindmappeId(id)).toList();
		List<Long> numbers2 = List.of(5l, 6l, 7l, 8l, 9l);
		List<KindmappeId> ids2 = numbers2.stream().map(id -> new KindmappeId(id)).toList();
		Mockito.when(service.loadKitafinderKindmappenIds(Mockito.anyInt(), Mockito.anyInt())).thenReturn(numbers1, numbers2, List.of());

		sut.open(null);

		assertThat(sut.read()).isEqualTo(ids1.get(0));
		assertThat(sut.read()).isEqualTo(ids1.get(1));
		assertThat(sut.read()).isEqualTo(ids1.get(2));
		assertThat(sut.read()).isEqualTo(ids1.get(3));
		assertThat(sut.read()).isEqualTo(ids1.get(4));
		assertThat(sut.read()).isEqualTo(ids2.get(0));
		assertThat(sut.read()).isEqualTo(ids2.get(1));
		assertThat(sut.read()).isEqualTo(ids2.get(2));
		assertThat(sut.read()).isEqualTo(ids2.get(3));
		assertThat(sut.read()).isEqualTo(ids2.get(4));
		assertThat(sut.read()).isNull();

		Mockito.verify(service, times(3)).loadKitafinderKindmappenIds(Mockito.anyInt(), Mockito.anyInt());
	}
}