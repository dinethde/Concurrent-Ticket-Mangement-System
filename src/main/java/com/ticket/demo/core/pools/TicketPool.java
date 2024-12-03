package com.ticket.demo.core.pools;

import com.ticket.demo.core.Ticket;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Slf4j
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

    private ConcurrentHashMap<String, Ticket> ticketArray = new ConcurrentHashMap<>();

    public TicketPool(TicketPool ticketPool) {
        this.ticketPoolName = ticketPool.ticketPoolName;
        this.vendorId = ticketPool.vendorId;
        this.maxTicketCapacity = ticketPool.maxTicketCapacity;
        this.eventId = ticketPool.eventId;

        generateTickets();
    }

    public TicketPool() {

    }

    public void generateTickets() {
        for (int i = 1; i <= maxTicketCapacity; i++) {
            Ticket ticket = new Ticket(eventId, null); // Consumer ID is null for unsold tickets
            ticketArray.put(ticket.getTicketId(), ticket); // Store in the map
        }
        log.info(" Array size : "+ticketArray.size());
    }

    public void printTickets() {
        for (int i = 0; i < ticketArray.size(); i++) {
            log.info(" Ticket : "+i+" : " +ticketArray.get((i+1)+"").getTicketId());
        }
    }

    public synchronized Ticket buyTicket(String consumerId) {
        // Check if there are any available tickets
        for (Map.Entry<String, Ticket> entry : ticketArray.entrySet()) {
            Ticket ticket = entry.getValue();

            // Use the `buy` method to lock and mark the ticket as sold
            if (ticket.buy()) {
                ticket.setConsumerId(consumerId); // Assign consumer to the ticket
                return ticket; // Return the sold ticket
            }
        }

        // No tickets available
        return null;
    }

    // Thread-safe method to add a ticket
    public synchronized void addTicket(Ticket ticket) {
        ticketArray.put(ticket.getTicketId(), ticket);
    }

    public synchronized Ticket getTicket(String ticketId) {
        return ticketArray.get(ticketId);
    }

    public synchronized int getAvailableTickets() {
        return ticketArray.size();
    }

}


