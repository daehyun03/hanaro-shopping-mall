package com.example.hanaro.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job dailySalesStatisticsJob;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정(00:00:00)에 실행
    public void runDailySalesStatisticsJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(dailySalesStatisticsJob, jobParameters);
        } catch (Exception e) {
            log.error("매출 통계 배치 Job 실행 중 에러가 발생했습니다.", e);
        }
    }
}
