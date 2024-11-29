package com.ticket.demo.core.pools;

import com.ticket.demo.core.Ticket;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Data
public class TicketPool {
    private int totalTickets;
    private final String ticketPoolName;
    private final int maxTicketCapacity;
    private Queue<Ticket> tickets = new LinkedList<>();
    private final String vendorId;
    private String ticketPoolDescription;
    private boolean ticketCategories; // true if categories exist
    private Map<String, Double> ticketPrice; // Category -> Price
    private Map<String, Integer> ticketCategoriesQnt; // Category -> Quantity
    private LocalDateTime date;


    public TicketPool(TicketPool ticketPool){
        this.ticketPoolName = ticketPool.ticketPoolName;
        this.vendorId = ticketPool.vendorId;
        this.maxTicketCapacity = ticketPool.maxTicketCapacity;

    }

    public TicketPool(String ticketPoolName, String vendorId, int maxTicketCapacity) {
        this.ticketPoolName = ticketPoolName;
        this.vendorId = vendorId;
        this.maxTicketCapacity = maxTicketCapacity;
    }

    // Thread-safe method to add a ticket
    public synchronized void addTicket(Ticket ticket) {
        while (tickets.size() >= maxTicketCapacity) {
            try {
                System.out.println("TicketPool is full. Waiting to add tickets...");
                wait(); // Block producer threads when pool is full
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
                return;
            }
        }
        tickets.add(ticket);
        totalTickets++;
        System.out.println("Added ticket: " + ticket.getTicketId() + " to pool " + ticketPoolName);
        notifyAll(); // Notify consumer threads that a ticket is available
    }

    // Thread-safe method to remove a ticket
    public synchronized Ticket removeTicket() {
        while (tickets.isEmpty()) {
            try {
                System.out.println("TicketPool is empty. Waiting for tickets...");
                wait(); // Block consumer threads when pool is empty
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
                return null;
            }
        }
        Ticket ticket = tickets.poll();
        totalTickets--;
        System.out.println("Removed ticket: " + ticket.getTicketId() + " from pool " + ticketPoolName);
        notifyAll(); // Notify producer threads that space is available
        return ticket;
    }

    public synchronized int getAvailableTickets() {
        return tickets.size();
    }

}

