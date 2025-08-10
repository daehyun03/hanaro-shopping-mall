package com.example.hanaro.config;

import com.example.hanaro.batch.DailySalesStatisticsTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    private final DailySalesStatisticsTasklet dailySalesStatisticsTasklet;

    @Bean
    public Job dailySalesStatisticsJob(JobRepository jobRepository, Step dailySalesStatisticsStep) {
        return new JobBuilder("dailySalesStatisticsJob", jobRepository)
                .start(dailySalesStatisticsStep)
                .build();
    }

    @Bean
    public Step dailySalesStatisticsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("dailySalesStatisticsStep", jobRepository)
                .tasklet(dailySalesStatisticsTasklet, transactionManager)
                .build();
    }
}
