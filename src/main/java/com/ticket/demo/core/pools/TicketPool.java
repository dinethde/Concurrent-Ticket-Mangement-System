package com.ticket.demo.core.pools;

import com.ticket.demo.core.Ticket;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class TicketPool {
    private int totalTickets;
    private String ticketPoolName;
    private int maxTicketCapacity;
    private String vendorId;
    private String ticketPoolDescription;
    private boolean ticketCategories; // true if categories exist
    private Map<String, Double> ticketPrice; // Category -> Price
    private Map<String, Integer> ticketCategoriesQnt; // Category -> Quantity
    private LocalDateTime date;

    private ConcurrentHashMap<Integer, Ticket> ticketArray = new ConcurrentHashMap<>();

    public TicketPool(TicketPool ticketPool) {
        this.ticketPoolName = ticketPool.ticketPoolName;
        this.vendorId = ticketPool.vendorId;
        this.maxTicketCapacity = ticketPool.maxTicketCapacity;
    }

    public TicketPool() {

    }

    // Thread-safe method to add a ticket
    public synchronized void addTicket(Ticket ticket) {
        ticketArray.put(ticket.getTicketId(), ticket);
    }

    public synchronized void getTicket(int ticketId) {
        ticketArray.get(ticketId);
    }

    // Thread-safe method to remove a ticket
    public synchronized Ticket removeTicket() {
        while (ticketArray.isEmpty()) {
            try {
                System.out.println("TicketPool is empty. Waiting for tickets...");
                wait(); // Block consumer threads when pool is empty
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
                return null;
            }
        }
        Ticket ticket = ticketArray.poll();
        totalTickets--;
        System.out.println("Removed ticket: " + ticket.getTicketId() + " from pool " + ticketPoolName);
        notifyAll(); // Notify producer threads that space is available
        return ticket;
    }

    public synchronized int getAvailableTickets() {
        return ticketArray.size();
    }

}


// This is my ticket pool class can you find cause for the error z
