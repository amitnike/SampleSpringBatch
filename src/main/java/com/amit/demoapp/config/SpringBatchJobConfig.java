package com.amit.demoapp.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.JsonLineMapper;
import org.springframework.batch.item.file.separator.JsonRecordSeparatorPolicy;
import org.springframework.batch.item.file.separator.RecordSeparatorPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.amit.demoapp.dto.EventDTO;
import com.amit.demoapp.listener.JobCompletionNotificationListener;
import com.amit.demoapp.mapper.WrappedJsonLineMapper;
import com.amit.demoapp.processor.EventItemProcessor;

@Configuration
@EnableBatchProcessing
public class SpringBatchJobConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public ItemReader<EventDTO> eventItemReader() {
		FlatFileItemReader<EventDTO> reader = new FlatFileItemReader<EventDTO>();
		reader.setResource(new ClassPathResource("logFile.txt"));
		reader.setRecordSeparatorPolicy(eventRecordSeparatorPolicy());
		reader.setLineMapper(eventLineMapper());
		return reader;
	}

	@Bean
	public LineMapper<EventDTO> eventLineMapper() {
		WrappedJsonLineMapper lineMapper = new WrappedJsonLineMapper();
		lineMapper.setDelegate(targetEventsLineMapper());
		return lineMapper;
	}

	@Bean
	public JsonLineMapper targetEventsLineMapper() {
		return new JsonLineMapper();
	}

	@Bean
	public RecordSeparatorPolicy eventRecordSeparatorPolicy() {
		return new JsonRecordSeparatorPolicy();
	}

	@Bean
	public EventItemProcessor processor() {
		return new EventItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<EventDTO> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<EventDTO>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO EventDetails (event_id, event_type,event_host) VALUES (:id, :type, :host)")
				.dataSource(dataSource).build();
	}

	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("importUserJob").listener(listener).flow(step1).end().build();
	}

	@Bean
	public Step step1(JdbcBatchItemWriter<EventDTO> writer) {
		return stepBuilderFactory.get("step1").<EventDTO, EventDTO>chunk(10).reader(eventItemReader())
				.processor(processor()).writer(writer).build();
	}

}
