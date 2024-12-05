package com.ticket.demo.core;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class Ticket {
    private static AtomicInteger ticketCounter = new AtomicInteger(1);

    private String ticketId;
    private String eventId;
    private String consumerId;
    private String ticketCategory;
    private double ticketPrice;
    private boolean isSold = false; // Indicates if the ticket is sold

    private final ReentrantLock lock = new ReentrantLock(); // Lock for this ticket

    public Ticket(String eventId, String consumerId) {
        this.eventId = eventId;
        this.ticketId = generateTicketId();
        this.consumerId = consumerId;
    }

    public Ticket(){}

    public boolean buy() {
        lock.lock();
        try {
            if (!isSold) {
                isSold = true;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public String generateTicketId() {
        return ticketCounter.getAndIncrement()+"";
    }

    public static void resetTicketCounter() {
        ticketCounter.set(1);
    }
}