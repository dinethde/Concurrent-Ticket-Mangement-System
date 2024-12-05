package com.ticket.demo.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ticket.demo.core.pools.TicketPool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Data
public class Consumer implements Runnable {
    private static AtomicInteger CONSUMERID = new AtomicInteger(1);
    private String consumerId;
    private String consumerName;
    private String consumerEmail;
    private String consumerPassword;
    @JsonIgnore
    private volatile boolean running = true; // Used to control the thread lifecycle
    @JsonIgnore
    private final BlockingQueue<TicketPool> taskQueue = new LinkedBlockingQueue<>(); // Tasks for this consumer
    @JsonIgnore
    private ConcurrentHashMap<String, Ticket> consumerTicketsList = new ConcurrentHashMap<>();

    public Consumer(Consumer consumer) {
        this.consumerName = consumer.consumerName;
        this.consumerEmail = consumer.consumerEmail;
        this.consumerId = generateConsumerId();
    }

    public Consumer() {
    }

    public void addTask(TicketPool ticketPool) {
        try {
            taskQueue.put(ticketPool); // Blocks if the queue is full
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("Consumer [{}] was interrupted while adding a task.", consumerName);
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Wait for a task from the queue
                TicketPool ticketPool = taskQueue.take(); // Blocks until a task is available
                Ticket ticket = ticketPool.buyTicket(consumerId); // Attempt to buy a ticket from the pool

                if (ticket != null) {
                    consumerTicketsList.put(consumerId, ticket);
                    log.info("{} successfully purchased ticket {}", consumerName, ticket.getTicketId());

                } else {
                    log.info("{} could not purchase a ticket from pool ", consumerName);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("Consumer [{}] interrupted.", consumerName);
            }
        }
    }

    public String generateConsumerId() {
        return CONSUMERID.getAndIncrement()+"";
    }
}