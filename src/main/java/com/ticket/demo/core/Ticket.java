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

    private boolean isSold = false; // Indicates if the ticket is sold
    private final ReentrantLock lock = new ReentrantLock(); // Lock for this ticket


    public Ticket(String eventId, String consumerId) {
        this.eventId = eventId;
        this.consumerId = consumerId;
        this.ticketId = generateTicketId() + "";
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
    public int generateTicketId() {
        return ticketCounter.getAndIncrement();
    }
}
