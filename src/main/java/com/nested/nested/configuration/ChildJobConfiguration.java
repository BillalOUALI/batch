package com.nested.nested.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.core.repository.JobRepository;

@Configuration
public class ChildJobConfiguration {

    @Bean
    Step loadDataStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("loadDataStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> Loading data");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    Step validateDataStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("validateDataStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> Validating data");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    Job childJob(JobRepository jobRepository, Step loadDataStep, Step validateDataStep) {
        return new JobBuilder("childJob", jobRepository)
                .start(loadDataStep)
                .next(validateDataStep)
                .build();
    }
}

