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
