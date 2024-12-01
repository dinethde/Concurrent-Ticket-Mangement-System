package com.ticket.demo.core;

import lombok.Data;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class Ticket {
    private int ticketId;
    private String eventId;

    private boolean isSold = false; // Indicates if the ticket is sold
    private final ReentrantLock lock = new ReentrantLock(); // Lock for this ticket

    private String consumerId;

    public Ticket(String eventId, String consumerId) {
        this.eventId = eventId;
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
}
