package de.muenchen.rbs.kitafinderdatenservice;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.retry.annotation.EnableRetry;

import de.muenchen.rbs.kitafinderdatenservice.batch.JobCompletionListener;
import de.muenchen.rbs.kitafinderdatenservice.batch.KindExportResultWriter;
import de.muenchen.rbs.kitafinderdatenservice.batch.KindmappenIdDeleteTasklet;
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
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor("kitafinder_datenservice");
	}

	@Bean
	public ItemWriter<KindmappeId> idWriter(KindmappeIdRepository idRepository) {
		return new RepositoryItemWriterBuilder<KindmappeId>().repository(idRepository).build();
	}

	@Bean
	public Job kitafinderImportJob(JobRepository jobRepository,
			Step idImportStep,
			Step idDeleteStep,
			JobCompletionListener listener) {
		return new JobBuilder("kitafinderImportJob", jobRepository)
				.listener(listener)
				.start(idDeleteStep)
				.next(idImportStep)
				.build();
	}

	@Bean
	public Step idImportStep(JobRepository jobRepository,
			JpaTransactionManager transactionManager,
			ItemReader<KindmappeId> reader,
			ItemWriter<KindmappeId> writer,
			@Value("${app.kitafinder.id-batch-size:100}") int batchSize) {
		return new StepBuilder("idImportStep", jobRepository)
				.<KindmappeId, KindmappeId>chunk(batchSize, transactionManager)
				.reader(reader)
				.writer(writer)
				.build();
	}

	@Bean
	public Step idDeleteStep(JobRepository jobRepository,
			JpaTransactionManager transactionManager,
			KindmappenIdDeleteTasklet task) {
		return new StepBuilder("idDeleteStep", jobRepository)
				.tasklet(task, transactionManager)
				.build();
	}

	@Bean
	public Step dataImportStep(TaskExecutor taskExecutor,
			JobRepository jobRepository,
			JpaTransactionManager transactionManager,
			KindmappenRestReader reader,
			KindmappenProcessor mappingProcessor,
			KindExportResultWriter writer,
			@Value("${app.kitafinder.data-batch-size:50}") int batchSize) {
		SynchronizedItemStreamReader<KindmappeDTO> syncronousReader = new SynchronizedItemStreamReader<>();
		syncronousReader.setDelegate(reader);

		return new StepBuilder("dataImportStep", jobRepository)
				.<KindmappeDTO, KindExportResult>chunk(batchSize, transactionManager)
				.reader(syncronousReader)
				.processor(mappingProcessor)
				.writer(writer)
				.taskExecutor(taskExecutor).build();
	}
}