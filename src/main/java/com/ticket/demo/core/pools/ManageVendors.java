package com.ticket.demo.core.pools;

import com.ticket.demo.core.Vendor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ManageVendors {
    private final HashMap<String, Vendor> vendorList = new HashMap<>();
    private final HashMap<String, Thread> vendorThreads = new HashMap<>();

    @Autowired
    ManageTicketPool manageTicketPool;

    // Add a new vendor and start its thread
    public synchronized void createVendors(Vendor vendor) {
        Vendor newVendor = new Vendor(vendor.getVendorName(), vendor.getVendorId());
        vendorList.put(newVendor.getVendorId(), newVendor);

        Thread vendorThread = new Thread(newVendor);
        vendorThreads.put(newVendor.getVendorId(), vendorThread);
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
            manageTicketPool.addTicketPool(ticketPool); //
            log.info("Task assigned to Vendor [{}] for event [{}]", vendor.getVendorName(), ticketPool.getTicketPoolName());

        } else {
            log.warn("Vendor with ID [{}] not found. Skipping event creation.", ticketPool.getVendorId());
        }
    }

    public void writeVendors(FileWriter writer) throws IOException {
        writer.write("\n=========== Vendors ===========\n");
        for (Map.Entry<String, Vendor> entry : vendorList.entrySet()) {
            Vendor vendor = entry.getValue();
            writer.write("Vendor Name: " + vendor.getVendorName()
                    + " | Vendor ID: " + vendor.getVendorId()
                    + " | Assigned Ticket Pools: " + vendor.getVendorEventList().size() + "\n");

            // List assigned ticket pools
            writer.write("  Assigned Events:\n");
            for (Map.Entry<String, TicketPool> ticketPoolEntry: vendor.getVendorEventList().entrySet()){
                TicketPool pool = ticketPoolEntry.getValue();
                writer.write("    Event: " + pool.getEventId()
                        + " | Pool Name: " + pool.getTicketPoolName()
                        + " | Max Capacity: " + pool.getMaxTicketCapacity() + "\n");
            }
        }
    }
}

// This is my manage vendor class and im getting an error like below
