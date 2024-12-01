package com.ticket.demo.core.pools;

import com.ticket.demo.core.Ticket;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ManageTicketPool {
    private final ConcurrentHashMap<String, TicketPool> ticketPoolArray = new ConcurrentHashMap<>();

    public void addTicketPool(TicketPool ticketPool) {
        ticketPoolArray.put(ticketPool.getEventId(), ticketPool);
    }

    public TicketPool getTicketPool(String ticketPoolId) {
        return ticketPoolArray.get(ticketPoolId);
    }

    public void writeTicketPools(FileWriter writer) throws IOException {
        writer.write("\n=========== Ticket Pools ===========\n");
        for (Map.Entry<String, TicketPool> entry : ticketPoolArray.entrySet()) {
            TicketPool pool = entry.getValue();
            writer.write("Event: " + pool.getEventId()
                    + " | Pool Name: " + pool.getTicketPoolName()
                    + " | Total Tickets: " + pool.getMaxTicketCapacity()
                    + " | Tickets Sold: " + pool.getTicketsSold()
                    + " | Tickets Available: " + (pool.getMaxTicketCapacity() - pool.getTicketsSold()) + "\n");

            // Write Ticket Details
            writer.write("  Tickets:\n");
            for (Map.Entry<String, Ticket> ticketEntry : pool.getTicketArray().entrySet()) {
                Ticket ticket = ticketEntry.getValue();
                writer.write("    Ticket ID: " + ticket.getTicketId()
                        + " | Status: " + (ticket.isSold() ? "Sold" : "Available")
                        + " | Consumer ID: " + (ticket.getConsumerId() != null ? ticket.getConsumerId() : "None") + "\n");
            }
        }
    }
}
