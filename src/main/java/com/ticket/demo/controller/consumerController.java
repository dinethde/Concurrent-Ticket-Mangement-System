package com.ticket.demo.controller;

import com.ticket.demo.core.Consumer;
import com.ticket.demo.core.Ticket;
import com.ticket.demo.core.pools.ManageConsumers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class consumerController {

    @Autowired
    ManageConsumers manageConsumers;

    @PostMapping("/create-customer")
    public void createConsumer(@RequestBody Consumer consumer) {
        manageConsumers.createConsumers(consumer);
    }

    @PostMapping("/buy-ticket")
    public void buyTicket(@RequestBody Ticket ticket) {
        manageConsumers.buyTicket(ticket);
    }

}

