package com.ticket.demo.controller;

import com.ticket.demo.ReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenerateReportController {

    @Autowired
    ReportGenerator reportGenerator;

    @PostMapping("/generate-report")
    public void generateReport() {
        reportGenerator.generateReport();
    }
}
