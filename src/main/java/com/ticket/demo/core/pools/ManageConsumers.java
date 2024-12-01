package com.ticket.demo.core.pools;

import com.ticket.demo.core.Consumer;
import com.ticket.demo.core.ManageTicketPool;
import com.ticket.demo.core.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
public class ManageConsumers {
    private final HashMap<String, Consumer> consumerList = new HashMap<>();
    private final HashMap<String, Thread> consumerThreads = new HashMap<>();

    // Add a new Consumer and start its thread
    public synchronized void createConsumers(Consumer consumer) {
        Consumer newConsumer = new Consumer(consumer);
        consumerList.put(newConsumer.getConsumerId(), newConsumer);

        Thread ConsumerThread = new Thread(newConsumer);
        consumerThreads.put(newConsumer.getConsumerId(), ConsumerThread);
        ConsumerThread.start();

        log.info("Consumer [{}] added and thread started.", newConsumer.getConsumerName());
    }

    // Assign an event creation task to the appropriate Consumer
    public synchronized void buyTicket(Ticket ticket) {
        Consumer consumer1;
        ManageTicketPool ticketPoolManager = new ManageTicketPool();
        synchronized (consumerList) {
            consumer1 = consumerList.get(ticket.getConsumerId());
        }
        if (consumer1 != null) {
            TicketPool ticketPool = ticketPoolManager.getTicketPool(ticket.getEventId());
            System.out.println(ticketPool);

            consumer1.addTask(ticketPool); // Add the event creation task to the Consumer's queue

            log.info("Task assigned to Consumer [{}] for event [{}]", consumer1.getConsumerName(), ticketPool.getTicketPoolName());
        } else {
            log.warn("Consumer with ID not found. Skipping event creation.");
        }
    }
}
