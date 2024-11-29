package com.ticket.demo.core;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Ticket {
    private int ticketId;
    private String ticketCategory;
    private LocalDateTime timeStamp;

    public Ticket(int ticketId, String ticketCategory) {
        this.ticketId = ticketId;
        this.ticketCategory = ticketCategory;
        this.timeStamp = LocalDateTime.now();
    }

}
