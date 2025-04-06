package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobDurationListener extends JobExecutionListenerSupport {

    private long startTime;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        long minutes = duration / 60000;
        long seconds = (duration % 60000) / 1000;
        long milliseconds = duration % 1000;
        log.warn("Job execution time: {} minutes {} seconds {} milliseconds", minutes, seconds, milliseconds);    }
}