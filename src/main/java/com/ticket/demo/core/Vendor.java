package com.ticket.demo.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ticket.demo.core.pools.TicketPool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Slf4j
public class Vendor implements Runnable {
    private static AtomicInteger SVENDORID = new AtomicInteger(1);
    private String vendorName;
    private String vendorId;
    private String vendorPassword;

    @JsonIgnore
    private boolean running = true;
    @JsonIgnore
    private ConcurrentHashMap<String, TicketPool> vendorEventList = new ConcurrentHashMap<>();
    @JsonIgnore
    private BlockingQueue<TicketPool> vendorTaskQueue = new LinkedBlockingQueue<>();

    public Vendor(String vendorName, String vendorId) {
        this.vendorName = vendorName;
        this.vendorId = generateVendorId();
    }

    // Add a task to the vendor's queue
    public synchronized void addTask(TicketPool ticketPool) {
        vendorTaskQueue.add(ticketPool);
        vendorEventList.put(ticketPool.getVendorId(), ticketPool);
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Wait for a task from the queue
                TicketPool ticketPool = vendorTaskQueue.take(); // Blocks until a task is available
                vendorCreateEvent(ticketPool);

            } catch (InterruptedException e) {
                log.info("Vendor [{}] interrupted.", vendorName);
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.info("Vendor [{}] stopped.", vendorName);
    }

    // Here I used the method you suggest. But isn't this memory insufficient?

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

    public int getVendorEventListSize() {
        return vendorEventList.size();
    }

    public String generateVendorId() {
        return SVENDORID.getAndIncrement()+"";
    }
}