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
    private String eventId;
    private String ticketPoolName;
    private int maxTicketCapacity;
    private String vendorId;
    private String ticketPoolDescription;
    private boolean ticketCategories; // true if categories exist
    private Map<String, Double> ticketPrice; // Category -> Price
    private Map<String, Integer> ticketCategoriesQnt; // Category -> Quantity
    private LocalDateTime date;
    private int ticketsSold = 0; // Tracks the number of tickets sold

    private ConcurrentHashMap<Integer, Ticket> ticketArray = new ConcurrentHashMap<>();

    public TicketPool(TicketPool ticketPool) {
        this.ticketPoolName = ticketPool.ticketPoolName;
        this.vendorId = ticketPool.vendorId;
        this.maxTicketCapacity = ticketPool.maxTicketCapacity;
    }

    public TicketPool() {

    }

    public synchronized Ticket buyTicket(String consumerId) {
        if (ticketsSold < maxTicketCapacity) { // Check if tickets are still available
            ticketsSold++;
            Ticket newTicket = new Ticket(eventId, consumerId); // Create a new ticket
            newTicket.setTicketId(ticketsSold); // Assign a unique ID
            ticketArray.put(newTicket.getTicketId(), newTicket); // Add to the map
            return newTicket;
        }
        return null; // No tickets available
    }

    // Thread-safe method to add a ticket
    public synchronized void addTicket(Ticket ticket) {
        ticketArray.put(ticket.getTicketId(), ticket);
    }

    public synchronized Ticket getTicket(int ticketId) {
        return ticketArray.get(ticketId);
    }

    public synchronized int getAvailableTickets() {
        return ticketArray.size();
    }
}


