package com.example.springbatch.controller;

import com.example.springbatch.service.BatchProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class BatchProcessingController {
    @Autowired
    private BatchProcessingService batchProcessingService;

    @GetMapping("/dataload")
    public String loadData() {
        return batchProcessingService.startExecution();
    }
}
