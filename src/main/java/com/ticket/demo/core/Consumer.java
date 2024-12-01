package com.ticket.demo.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ticket.demo.core.pools.TicketPool;
import lombok.Data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


@Data
public class Consumer implements Runnable {
    private String consumerId;
    private String consumerName;
    private String consumerEmail;
    @JsonIgnore
    private String ticketId;
    @JsonIgnore
    private volatile boolean running = true; // Used to control the thread lifecycle
    @JsonIgnore
    private final BlockingQueue<TicketPool> taskQueue = new LinkedBlockingQueue<>(); // Tasks for this consumer
    private ConcurrentHashMap<String, Ticket> consumerTicketsList = new ConcurrentHashMap<>();

    public Consumer(Consumer consumer) {
        this.consumerName = consumer.consumerName;
        this.consumerEmail = consumer.consumerEmail;
        this.consumerId = consumer.consumerId;
    }

    public Consumer(){}

    public void addTask(TicketPool ticketPool) {
        try {
            taskQueue.put(ticketPool); // Blocks if the queue is full
            } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Consumer [" + consumerName + "] was interrupted while adding a task.");
        }
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Wait for a task from the queue
                TicketPool ticketPool = taskQueue.take(); // Blocks until a task is available
                Ticket ticket = ticketPool.buyTicket(consumerId); // Attempt to buy a ticket from the pool
                consumerTicketsList.put(consumerId, ticket);

                if (ticket != null && ticket.buy()) {
                    System.out.println(consumerName + " successfully purchased ticket " + ticket.getTicketId());
                } else {
                    System.out.println(consumerName + " could not purchase a ticket from pool ");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Consumer [" + consumerName + "] interrupted.");
            }
        }
    }

}