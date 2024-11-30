package com.ticket.demo.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ticket.demo.core.pools.TicketPool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Data
@Slf4j
public class Vendor implements Runnable {
    private String vendorName;
    private String id;
    @JsonIgnore
    private BlockingQueue<TicketPool> taskQueue = new LinkedBlockingQueue<>();
    @JsonIgnore
    private boolean running = true;

    public Vendor(String vendorName, String id) {
        this.vendorName = vendorName;
        this.id = id;
    }

    // Add a task to the vendor's queue
    public void addTask(TicketPool ticketPool) {
        taskQueue.add(ticketPool);
    }

    // Stop the vendor thread gracefully
    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Wait for a task from the queue
                TicketPool ticketPool = taskQueue.take(); // Blocks until a task is available
                vendorCreateEvent(ticketPool);

            } catch (InterruptedException e) {
                log.info("Vendor [{}] interrupted.", vendorName);
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.info("Vendor [{}] stopped.", vendorName);
    }

    private void vendorCreateEvent(TicketPool ticketPool) {
        log.info("[{}] Vendor [{}] is creating event [{}]",
                LocalDateTime.now(), vendorName, ticketPool.getTicketPoolName());

        // Simulate work (optional)
            try {
                Thread.sleep(5000); // Optional simulation delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        log.info("[{}] Vendor [{}] created event [{}] successfully.",
                LocalDateTime.now(), vendorName, ticketPool.getTicketPoolName());
    }
}


// In above code what while loop inside run method do and what is stop method do