package de.muenchen.rbs.kitafinderdatenservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import de.muenchen.rbs.kitafinderdatenservice.batch.KindmappenRestReader;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.adapter.KitafinderExportService;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KindmappeDTO;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KitafinderExportDTO;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindmappeIdRepository;

public class KindmappenRestReaderTest {

	private TestDataUtils testDataUtils = new TestDataUtils();
	private KitafinderExportService service = Mockito.mock(KitafinderExportService.class);
	private KindmappeIdRepository idRepository = Mockito.mock(KindmappeIdRepository.class);

	private KindmappenRestReader sut;

	@BeforeEach
	private void setup() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(3);
		executor.setMaxPoolSize(3);
		executor.setQueueCapacity(Integer.MAX_VALUE);
		executor.setThreadNamePrefix("batch-kitafinder-export-test-");
		executor.initialize();

		this.sut = new KindmappenRestReader(3, executor, idRepository, service);
	}

	@Test
	public void testReadSinglePage() throws Exception {
		Page<KindmappeId> ids = new PageImpl<>(List.of(1l, 2l, 3l).stream().map(id -> new KindmappeId(id)).toList(),
				Pageable.ofSize(3), 3);
		Mockito.when(idRepository.findAll(Mockito.any(Pageable.class))).thenReturn(ids);
		KitafinderExportDTO result = new KitafinderExportDTO();
		List<KindmappeDTO> kindmappen = List.of(testDataUtils.fakeKindmappeDTO(), testDataUtils.fakeKindmappeDTO(),
				testDataUtils.fakeKindmappeDTO());
		result.setKindMappen(kindmappen);
		Mockito.when(service.loadKitafinderData(Mockito.any())).thenReturn(result);

		sut.open(null);

		assertThat(sut.read()).isEqualTo(kindmappen.get(0));
		assertThat(sut.read()).isEqualTo(kindmappen.get(1));
		assertThat(sut.read()).isEqualTo(kindmappen.get(2));
		assertThat(sut.read()).isNull();

		Mockito.verify(idRepository, times(1)).findAll(Mockito.any(Pageable.class));
		Mockito.verify(service, times(1)).loadKitafinderData(Mockito.any());
	}

	@Test
	public void testReadMultiplePages() throws Exception {
		Page<KindmappeId> ids1 = new PageImpl<>(List.of(1l, 2l, 3l).stream().map(id -> new KindmappeId(id)).toList(),
				Pageable.ofSize(3).first(), 5);
		Page<KindmappeId> ids2 = new PageImpl<>(List.of(4l, 5l).stream().map(id -> new KindmappeId(id)).toList(),
				Pageable.ofSize(3).first().next(), 5);
		Mockito.when(idRepository.findAll(Mockito.any(Pageable.class))).thenReturn(ids1, ids2);

		List<KindmappeDTO> kindmappen = List.of(testDataUtils.fakeKindmappeDTO(), testDataUtils.fakeKindmappeDTO(),
				testDataUtils.fakeKindmappeDTO(), testDataUtils.fakeKindmappeDTO(), testDataUtils.fakeKindmappeDTO());
		KitafinderExportDTO result1 = new KitafinderExportDTO();
		result1.setKindMappen(kindmappen.subList(0, 3));
		KitafinderExportDTO result2 = new KitafinderExportDTO();
		result2.setKindMappen(kindmappen.subList(3, 5));

		Mockito.when(service.loadKitafinderData(Mockito.eq(List.of(1l, 2l, 3l)))).thenReturn(result1);
		Mockito.when(service.loadKitafinderData(Mockito.eq(List.of(4l, 5l)))).thenReturn(result2);

		sut.open(null);

		assertThat(sut.read()).isEqualTo(kindmappen.get(0));
		assertThat(sut.read()).isEqualTo(kindmappen.get(1));
		assertThat(sut.read()).isEqualTo(kindmappen.get(2));
		assertThat(sut.read()).isEqualTo(kindmappen.get(3));
		assertThat(sut.read()).isEqualTo(kindmappen.get(4));
		assertThat(sut.read()).isNull();

		Mockito.verify(idRepository, times(2)).findAll(Mockito.any(Pageable.class));
		Mockito.verify(service, times(1)).loadKitafinderData(Mockito.eq(List.of(1l, 2l, 3l)));
		Mockito.verify(service, times(1)).loadKitafinderData(Mockito.eq(List.of(4l, 5l)));
		Mockito.verify(service, times(2)).loadKitafinderData(Mockito.any());
	}
}