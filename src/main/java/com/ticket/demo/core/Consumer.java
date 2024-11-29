package com.ticket.demo.core;

import com.ticket.demo.core.pools.TicketPool;
import lombok.Data;

@Data
public class Consumer implements Runnable { // Implement Runnable to make Consumer multi-threaded
    private String consumerName;
    private String consumerEmail;
    private Ticket ticketId; // Holds the ticket purchased by the consumer

    private TicketPool ticketPool; // Shared TicketPool
    private String category; // Ticket category to buy from
    private int retrievalRate; // Rate in milliseconds at which tickets are bought

    private volatile boolean running = true; // Used to control the thread lifecycle

    public Consumer(String consumerName, String consumerEmail, TicketPool ticketPool, String category, int retrievalRate) {
        this.consumerName = consumerName;
        this.consumerEmail = consumerEmail;
        this.ticketPool = ticketPool;
        this.category = category;
        this.retrievalRate = retrievalRate; // Time in milliseconds to simulate delay between purchases
    }

    @Override
    public void run() {
        while (running) { // Continuously buy tickets until stopped
            try {
                Thread.sleep(retrievalRate); // Simulate delay in ticket retrieval
                synchronized (ticketPool) { // Synchronize to ensure safe interaction with the shared TicketPool
                    if (ticketPool.getAvailableTickets() > 0) {
                        this.ticketId = ticketPool.removeTicket(); // Safely remove a ticket from the pool
                        if (ticketId != null) {
                            System.out.println(consumerName + " bought ticket ID: " + ticketId.getTicketId() +
                                    " in category: " + category);
                        }
                    } else {
                        System.out.println(consumerName + ": No tickets available in the selected category.");
                        ticketPool.wait(); // Wait if no tickets are available
                    }
                }
            } catch (InterruptedException e) {
                System.out.println(consumerName + " was interrupted.");
                Thread.currentThread().interrupt(); // Restore the interrupt status
                break;
            }
        }
    }

    public void stop() {
        running = false; // Set the flag to stop the thread
    }

    public void generateReceipt() {
        if (ticketId != null) {
            System.out.println("Receipt: Ticket ID - " + ticketId.getTicketId() +
                    ", Category - " + ticketId.getTicketCategory() +
                    ", Timestamp - " + ticketId.getTimeStamp());
        } else {
            System.out.println("No ticket purchased yet.");
        }
    }
}
