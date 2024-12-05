package com.ticket.demo.controller;

import com.ticket.demo.configuration.SystemConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UpdateController {
    
    @Autowired
    SystemConfiguration systemConfiguration;

    @PostMapping("/maxTickets/{maxTickets}")
    public void updateMaxTickets(@PathVariable int maxTickets) {
        log.info("max ticket capacity : {}",maxTickets);
        systemConfiguration.updateMaxTicketCapacity(maxTickets);
    }
}
