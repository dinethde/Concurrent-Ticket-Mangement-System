package com.ticket.demo.core.pools;

import com.ticket.demo.core.Consumer;
import com.ticket.demo.core.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ManageConsumers {
    private final HashMap<String, Consumer> consumerList = new HashMap<>();
    private final HashMap<String, Thread> consumerThreads = new HashMap<>();

    @Autowired
    ManageTicketPool ticketPoolManager;

    // Add a new Consumer and start its thread
    public synchronized void createConsumers(Consumer consumer) {
        Consumer newConsumer = new Consumer(consumer);
        consumerList.put(newConsumer.getConsumerId(), newConsumer);

        Thread consumerThread = new Thread(newConsumer);
        consumerThreads.put(newConsumer.getConsumerId(), consumerThread);
        consumerThread.start();

        log.info("Consumer [{}] added and thread started.", newConsumer.getConsumerName());
    }

    // Assign an event creation task to the appropriate Consumer
    public synchronized void buyTicket(Ticket ticket) {
        Consumer consumer1;
        synchronized (consumerList) {
            consumer1 = consumerList.get(ticket.getConsumerId());
        }
        if (consumer1 != null) {
            TicketPool ticketPool = ticketPoolManager.getTicketPool(ticket.getEventId());
            log.info("Ticket pool : " + ticketPool);

            consumer1.addTask(ticketPool); // Add the event creation task to the Consumer's queue

            log.info("Task assigned to Consumer [{}] for event [{}]", consumer1.getConsumerName(), ticketPool.getTicketPoolName());
        } else {
            log.warn("Consumer with ID not found. Skipping event creation.");
        }
    }

    public void writeConsumers(FileWriter writer) throws IOException {
        writer.write("\n=========== Consumers ===========\n");
        for (Map.Entry<String, Consumer> entry : consumerList.entrySet()) {
            Consumer consumer = entry.getValue();
            writer.write("Consumer: " + consumer.getConsumerName()
                    + " | ID: " + consumer.getConsumerId()
                    + " | Email: " + consumer.getConsumerEmail()
                    + " | Tickets Purchased: " + consumer.getConsumerTicketsList().size() + "\n");

            writer.write("  Bought tickets :\n");
            for (Map.Entry<String, Ticket> ticketEntry : consumer.getConsumerTicketsList().entrySet()) {
                Ticket ticket = ticketEntry.getValue();
                writer.write("    Ticket Id: " + ticket.getTicketId()
                        + " | Event Id: " + ticket.getEventId() + "\n");
            }
        }
    }
}
