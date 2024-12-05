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
    private static int initialized;

    private String eventId;
    private String ticketPoolName;
    private int maxTicketCapacity;
    private String vendorId;
    private String ticketPoolDescription;
    private String eventLocation;
    private LocalDateTime date;
    private int ticketsSold = 0; // Tracks the number of tickets sold

    private ConcurrentHashMap<String, Ticket> ticketArray;

    private boolean ticketCategories; // true if categories exist
    private Map<String, Double> ticketPrices; // Category -> Price
    private Map<String, Integer> ticketCategoriesQnt; // Category -> Quantity

    public TicketPool(TicketPool ticketPool) {
        this.ticketPoolName = ticketPool.ticketPoolName;
        this.vendorId = ticketPool.vendorId;
        this.maxTicketCapacity = ticketPool.maxTicketCapacity;
        this.eventId = ticketPool.eventId;

        this.ticketArray = new ConcurrentHashMap<>(maxTicketCapacity);
        generateTickets();
    }

    public TicketPool() {

    }

    public void generateTickets() {
        for (int i = 1; i <= maxTicketCapacity; i++) {
            Ticket ticket = new Ticket(eventId, null); // Consumer ID is null for unsold tickets
            ticketArray.put(ticket.getTicketId(), ticket); // Store in the map
        }
        log.info("Generated {} tickets. Array size: {}", maxTicketCapacity, ticketArray.size());
        printTickets();
    }

    public void printTickets() {
        for (Map.Entry<String, Ticket> entry : ticketArray.entrySet()) {
            log.info(" Ticket ID: {} : Consumer ID: {}", entry.getValue().getTicketId(), entry.getValue().getConsumerId());
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

    public synchronized Ticket getTicket(int ticketId) {
        return ticketArray.get(ticketId);
    }

    public synchronized int getAvailableTickets() {
        return ticketArray.size();
    }

}