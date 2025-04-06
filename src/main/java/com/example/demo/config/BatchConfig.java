package com.example.demo.config;

import com.example.demo.domain.Person;
import com.example.demo.processor.PersonItemProcessor;
import com.example.demo.reader.PersonItemReader;
import com.example.demo.repository.PersonRepository;
import com.example.demo.service.PersonService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.InputStream;

@Configuration
public class BatchConfig {

    private final PersonService personService;

    private String FILE_NAME = "people_sample.xlsx";

    public BatchConfig(PersonService personService) {
        this.personService = personService;
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step,
                   JobExecutionListener jobExecutionListener) {
        return new JobBuilder("importJob", jobRepository)
                .listener(jobExecutionListener)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      RepositoryItemWriter<Person> writer) throws Exception {
        return new StepBuilder("step", jobRepository)
                .<Person, Person>chunk(50000, transactionManager)
                .reader(reader())
                .processor(new PersonItemProcessor())
                .writer(writer)
                .build();
    }


    @Bean
    public PersonItemReader reader()  {
        InputStream in = getClass().getClassLoader().getResourceAsStream(FILE_NAME);

        if (in == null) {
            in = System.in;
        }
        return new PersonItemReader(personService, in);
    }
    @Bean
    public RepositoryItemWriter<Person> writer(PersonRepository personRepository) {
        RepositoryItemWriter<Person> writer = new RepositoryItemWriter<>();
        writer.setRepository(personRepository);
        writer.setMethodName("save");
        return writer;
    }
}
