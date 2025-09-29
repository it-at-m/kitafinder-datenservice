package de.muenchen.rbs.kitafinderdatenservice;

import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import de.muenchen.rbs.kitafinderdatenservice.batch.EventGenerationDeciderTasklet;
import de.muenchen.rbs.kitafinderdatenservice.batch.FailedRunCleanupTasklet;
import de.muenchen.rbs.kitafinderdatenservice.batch.JobCompletionListener;
import de.muenchen.rbs.kitafinderdatenservice.batch.KindDbReader;
import de.muenchen.rbs.kitafinderdatenservice.batch.KindEventProcessor;
import de.muenchen.rbs.kitafinderdatenservice.batch.KindExportResultWriter;
import de.muenchen.rbs.kitafinderdatenservice.batch.KindIdEventProcessor;
import de.muenchen.rbs.kitafinderdatenservice.batch.KindmappenIdDeleteTasklet;
import de.muenchen.rbs.kitafinderdatenservice.batch.KindmappenIdRestReader;
import de.muenchen.rbs.kitafinderdatenservice.batch.KindmappenProcessor;
import de.muenchen.rbs.kitafinderdatenservice.batch.KindmappenRestReader;
import de.muenchen.rbs.kitafinderdatenservice.batch.OldKindDeleteTasklet;
import de.muenchen.rbs.kitafinderdatenservice.batch.OutboxeventBatchWriter;
import de.muenchen.rbs.kitafinderdatenservice.batch.VertragIdEventProcessor;
import de.muenchen.rbs.kitafinderdatenservice.domain.Kind;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindExportResult;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;
import de.muenchen.rbs.kitafinderdatenservice.domain.events.Outboxevent;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KindmappeDTO;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindmappeIdRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.OutboxEventRepository;
import de.muenchen.rbs.kitafinderdatenservice.repository.VertragRepository;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
@ComponentScan
@EnableRetry
public class BatchConfiguration {

	@Bean
	public TaskExecutor taskExecutor(@Value("${app.kitafinder.thread-count:10}") int threadCount) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(threadCount);
	    executor.setMaxPoolSize(threadCount);
	    executor.setQueueCapacity(Integer.MAX_VALUE);
	    executor.setThreadNamePrefix("batch-kitafinder-export-");
	    executor.initialize();
	    return executor;
	}

	@Bean
	public ItemWriter<KindmappeId> idWriter(KindmappeIdRepository idRepository) {
		return new RepositoryItemWriterBuilder<KindmappeId>().repository(idRepository).build();
	}

	@Bean
	public ItemReader<Long> newKindReader(KindRepository repository) {
		return new RepositoryItemReaderBuilder<Long>()
				.repository(repository)
				.methodName("findNew")
				.sorts(Map.of())
				.saveState(false)
				.build();
	}

	@Bean
	public ItemReader<Long> newVertragReader(VertragRepository repository) {
		return new RepositoryItemReaderBuilder<Long>()
				.repository(repository)
				.methodName("findNew")
				.sorts(Map.of())
				.saveState(false)
				.build();
	}

	@Bean
	public ItemWriter<Outboxevent> eventWriter(OutboxEventRepository repository) {
		return new RepositoryItemWriterBuilder<Outboxevent>().repository(repository).build();
	}

	@Bean
	public Job kitafinderImportJob(JobRepository jobRepository, Step idImportStep, Step idDeleteStep,
			Step dataImportStep, Step eventGenerationDeciderStep, Step newKindEventStep, Step newVertragEventStep,
			Step cleanUpStep, Step oldDataDeleteStep, JobCompletionListener listener) {
		return new JobBuilder("kitafinderImportJob", jobRepository)
				.listener(listener)
				.start(idDeleteStep)
				.next(idImportStep)
				.next(dataImportStep)
				.on("*").to(cleanUpStep)
				.from(dataImportStep).on("COMPLETED").to(eventGenerationDeciderStep)
				.from(eventGenerationDeciderStep).on("*").to(oldDataDeleteStep)
				.from(eventGenerationDeciderStep).on("COMPLETED").to(newKindEventStep)
				.from(newKindEventStep).next(newVertragEventStep)
				.next(oldDataDeleteStep)
				.end()
				.build();
	}

	@Bean
	public Step idImportStep(TaskExecutor taskExecutor, JobRepository jobRepository,
			JpaTransactionManager transactionManager, KindmappenIdRestReader reader, ItemWriter<KindmappeId> writer,
			@Value("${app.kitafinder.id-batch-size:100}") int batchSize) {
		SynchronizedItemStreamReader<KindmappeId> syncronousReader = new SynchronizedItemStreamReader<>();
		syncronousReader.setDelegate(reader);

		return new StepBuilder("idImportStep", jobRepository)
				.<KindmappeId, KindmappeId>chunk(batchSize, transactionManager)
				.reader(syncronousReader)
				.writer(writer)
				.allowStartIfComplete(true)
				.taskExecutor(taskExecutor)
				.build();
	}

	@Bean
	public Step idDeleteStep(JobRepository jobRepository, JpaTransactionManager transactionManager,
			KindmappenIdDeleteTasklet task) {
		return new StepBuilder("idDeleteStep", jobRepository)
				.tasklet(task, transactionManager)
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step dataImportStep(TaskExecutor taskExecutor, JobRepository jobRepository,
			JpaTransactionManager transactionManager, KindmappenRestReader reader, KindmappenProcessor mappingProcessor,
			KindExportResultWriter writer, @Value("${app.kitafinder.data-batch-size:50}") int batchSize) {
		SynchronizedItemStreamReader<KindmappeDTO> syncronousReader = new SynchronizedItemStreamReader<>();
		syncronousReader.setDelegate(reader);

		return new StepBuilder("dataImportStep", jobRepository)
				.<KindmappeDTO, KindExportResult>chunk(batchSize, transactionManager)
				.reader(syncronousReader)
				.processor(mappingProcessor)
				.writer(writer)
				.taskExecutor(taskExecutor)
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step eventGenerationStep(TaskExecutor taskExecutor, JobRepository jobRepository,
			JpaTransactionManager transactionManager, KindDbReader kindDbReader, KindEventProcessor eventProcessor,
			OutboxeventBatchWriter eventWriter, @Value("${app.kitafinder.data-batch-size:50}") int batchSize) {
		SynchronizedItemStreamReader<Kind> syncronousReader = new SynchronizedItemStreamReader<>();
		syncronousReader.setDelegate(kindDbReader);
		
		return new StepBuilder("eventGenerationStep", jobRepository)
				.<Kind, List<Outboxevent>>chunk(batchSize, transactionManager)
				.reader(syncronousReader)
				.processor(eventProcessor)
				.writer(eventWriter)
				.taskExecutor(taskExecutor)
				.build();
	}

	@Bean
	public Step newKindEventStep(TaskExecutor taskExecutor, JobRepository jobRepository,
			JpaTransactionManager transactionManager, ItemReader<Long> newKindReader, KindIdEventProcessor eventProcessor,
			ItemWriter<Outboxevent> eventWriter, @Value("${app.kitafinder.data-batch-size:50}") int batchSize) {
		return new StepBuilder("newKindEventStep", jobRepository)
				.<Long, Outboxevent>chunk(batchSize, transactionManager)
				.reader(newKindReader)
				.processor(eventProcessor)
				.writer(eventWriter)
				.taskExecutor(taskExecutor)
				.build();
	}

	@Bean
	public Step newVertragEventStep(TaskExecutor taskExecutor, JobRepository jobRepository,
			JpaTransactionManager transactionManager, ItemReader<Long> newVertragReader, VertragIdEventProcessor eventProcessor,
			ItemWriter<Outboxevent> eventWriter, @Value("${app.kitafinder.data-batch-size:50}") int batchSize) {
		return new StepBuilder("newVertragEventStep", jobRepository)
				.<Long, Outboxevent>chunk(batchSize, transactionManager)
				.reader(newVertragReader)
				.processor(eventProcessor)
				.writer(eventWriter)
				.taskExecutor(taskExecutor)
				.build();
	}

	@Bean
	public Step oldDataDeleteStep(TaskExecutor taskExecutor, JobRepository jobRepository,
			JpaTransactionManager transactionManager, OldKindDeleteTasklet task) {
		return new StepBuilder("oldDataDeleteStep", jobRepository)
				.tasklet(task, transactionManager)
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step eventGenerationDeciderStep(TaskExecutor taskExecutor, JobRepository jobRepository,
			JpaTransactionManager transactionManager, EventGenerationDeciderTasklet task) {
		return new StepBuilder("eventGenerationDeciderStep", jobRepository)
				.tasklet(task, transactionManager)
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	public Step cleanUpStep(TaskExecutor taskExecutor, JobRepository jobRepository,
			JpaTransactionManager transactionManager, FailedRunCleanupTasklet task) {
		return new StepBuilder("cleanUpStep", jobRepository)
				.tasklet(task, transactionManager)
				.allowStartIfComplete(true)
				.build();
	}
}