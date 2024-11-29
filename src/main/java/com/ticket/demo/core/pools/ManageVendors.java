package com.ticket.demo.core.pools;

import com.ticket.demo.core.Vendor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
public class ManageVendors {
    private final HashMap<String, Vendor> vendorList = new HashMap<>(); // Vendor ID -> Vendor
    private final HashMap<String, Thread> vendorThreads = new HashMap<>(); // Vendor ID -> Vendor Thread

    // Add a new vendor and start its thread
    public synchronized void createVendors(Vendor vendor) {
        Vendor newVendor = new Vendor(vendor.getVendorName(), vendor.getId());
        vendorList.put(newVendor.getId(), newVendor);

        Thread vendorThread = new Thread(newVendor);
        vendorThreads.put(newVendor.getId(), vendorThread);
        vendorThread.start();

        log.info("Vendor [{}] added and thread started.", newVendor.getVendorName());
    }

    // Assign an event creation task to the appropriate vendor
    public synchronized void createEvents(TicketPool ticketPoolEvent) {
        TicketPool ticketPool = new TicketPool(ticketPoolEvent);
        Vendor vendor;
        synchronized (vendorList) {
            vendor = vendorList.get(ticketPool.getVendorId());
        }

        if (vendor != null) {
            vendor.addTask(ticketPool); // Add the event creation task to the vendor's queue
            log.info("Task assigned to Vendor [{}] for event [{}]", vendor.getVendorName(), ticketPool.getTicketPoolName());
        } else {
            log.warn("Vendor with ID [{}] not found. Skipping event creation.", ticketPool.getVendorId());
        }
    }
}
