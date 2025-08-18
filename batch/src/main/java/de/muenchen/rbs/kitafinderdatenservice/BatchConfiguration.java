package de.muenchen.rbs.kitafinderdatenservice;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import de.muenchen.rbs.kitafinderdatenservice.batch.JobCompletionListener;
import de.muenchen.rbs.kitafinderdatenservice.batch.KindExportResultWriter;
import de.muenchen.rbs.kitafinderdatenservice.batch.KindmappenIdDeleteTasklet;
import de.muenchen.rbs.kitafinderdatenservice.batch.KindmappenIdRestReader;
import de.muenchen.rbs.kitafinderdatenservice.batch.KindmappenProcessor;
import de.muenchen.rbs.kitafinderdatenservice.batch.KindmappenRestReader;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindExportResult;
import de.muenchen.rbs.kitafinderdatenservice.domain.KindmappeId;
import de.muenchen.rbs.kitafinderdatenservice.kitafinder.dto.KindmappeDTO;
import de.muenchen.rbs.kitafinderdatenservice.repository.KindmappeIdRepository;

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
	public Job kitafinderImportJob(JobRepository jobRepository, Step idImportStep, Step idDeleteStep,
			Step dataImportStep, JobCompletionListener listener) {
		return new JobBuilder("kitafinderImportJob", jobRepository)
				.listener(listener)
				.start(idDeleteStep)
				.next(idImportStep)
				.next(dataImportStep)
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
				.processor(mappingProcessor).
				writer(writer)
				.taskExecutor(taskExecutor)
				.build();
	}
}