package com.example.springbatch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
public class DbBatchProcessingService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("userTransferJob")
    private Job userTransferJob;

    public String startExecution() {
        startUserDataLoad();
        return ExitStatus.COMPLETED.getExitCode();
    }

    private void startUserDataLoad() {
        log.info("Starting User transfer job ...");
        StepExecution lastStepExecution = getLastStepExecution(userTransferJob);
        log.info("Exiting User transfer job ... " + lastStepExecution.getExitStatus().getExitCode());
    }

    private StepExecution getLastStepExecution(Job job) {
        Collection<StepExecution> stepExecutions = new ArrayList<>();
        try{
            stepExecutions = jobLauncher.run(job, new JobParameters()).getStepExecutions();
        } catch (Exception ex) {
            log.error("Exception occurred while running the ... " + job.getName());
        }

        return new ArrayList<>(stepExecutions).get(stepExecutions.size()-1);
    }
}
