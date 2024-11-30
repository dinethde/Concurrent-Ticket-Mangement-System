package com.ticket.demo.core;

import com.ticket.demo.core.pools.TicketPool;
import org.springframework.stereotype.Component;

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
}
