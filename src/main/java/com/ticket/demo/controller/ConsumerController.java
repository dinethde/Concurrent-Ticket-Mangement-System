package com.ticket.demo.controller;

import com.ticket.demo.core.Consumer;
import com.ticket.demo.core.Ticket;
import com.ticket.demo.core.pools.ManageConsumers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ConsumerController {

    @Autowired
    ManageConsumers manageConsumers;

    @PostMapping("/create-customer")
    public void createConsumer(@RequestBody Consumer consumer) {
        log.info(""+consumer);
        manageConsumers.createConsumers(consumer);
    }

    @PostMapping("/buy-ticket")
    public void buyTicket(@RequestBody Ticket ticket) {
        log.info(ticket+"");
        manageConsumers.buyTicket(ticket);
    }

}

