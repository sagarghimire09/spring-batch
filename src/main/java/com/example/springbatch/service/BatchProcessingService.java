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
import java.util.stream.Collectors;

@Service
@Slf4j
public class BatchProcessingService {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("userLoadJob")
    private Job userLoadJob;

    public String startExecution() {
        startUserDataLoad();
        startEmployeeDataLoad();

        return ExitStatus.COMPLETED.getExitCode();
    }

    private void startUserDataLoad() {
        log.info("Starting User data load ...");
        StepExecution lastStepExecution = getLastStepExecution(userLoadJob);
        log.info("Exiting User data load ... " + lastStepExecution.getExitStatus().getExitCode());
    }

    private void startEmployeeDataLoad() {
    }

    private StepExecution getLastStepExecution(Job job) {
        Collection<StepExecution> stepExecutions = new ArrayList<>();
        try{
            stepExecutions = jobLauncher.run(job, new JobParameters()).getStepExecutions();
        } catch (Exception ex) {
            log.error("Exception occurred while running the ... " + job.getName());
        }

        return stepExecutions.stream().collect(Collectors.toList()).get(stepExecutions.size()-1);
    }
}
