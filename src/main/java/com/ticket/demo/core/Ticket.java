package com.ticket.demo.core;

import java.time.LocalDateTime;

import lombok.Data;

import java.util.concurrent.locks.ReentrantLock;

@Data
public class Ticket {
    private final int id;
    private final String eventName;
    private String eventCategory;
    private boolean isSold = false; // Indicates if the ticket is sold
    private final ReentrantLock lock = new ReentrantLock(); // Lock for this ticket

    public Ticket(int id, String eventName) {
        this.id = id;
        this.eventName = eventName;
    }

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
