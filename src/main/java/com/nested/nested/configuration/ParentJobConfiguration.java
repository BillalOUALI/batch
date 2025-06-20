package com.nested.nested.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ParentJobConfiguration {

    @Bean
    Step initialStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("initialStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> Starting main job");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    Step childJobStep(JobRepository jobRepository,
    					PlatformTransactionManager transactionManager,
                      JobLauncher jobLauncher,
                      @Qualifier("childJob") Job childJob) {

        return new StepBuilder("childJobStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    JobParameters jobParameters = new JobParametersBuilder()
                            .addLong("timestamp", System.currentTimeMillis())
                            .toJobParameters();
                    jobLauncher.run(childJob, jobParameters);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    Job parentJob(JobRepository jobRepository,
                  PlatformTransactionManager transactionManager,
                  @Qualifier("initialStep") Step initialStep,
                  @Qualifier("childJobStep") Step childJobStep) {
        return new JobBuilder("parentJob", jobRepository)
                .start(initialStep)
                .next(childJobStep)
                .build();
    }

}



