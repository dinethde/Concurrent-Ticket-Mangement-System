package com.ticket.demo.core.pools;

import com.ticket.demo.configuration.ConfigObserver;
import com.ticket.demo.configuration.SystemConfiguration;
import com.ticket.demo.core.Vendor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ticket.demo.core.Ticket.resetTicketCounter;

@Slf4j
@Component
public class ManageVendors implements ConfigObserver {
    private final HashMap<String, Vendor> vendorList = new HashMap<>();
    private final HashMap<String, Thread> vendorThreads = new HashMap<>();
    private final SystemConfiguration systemConfig;
    private int totalVendors;
    private int totalNumberOfPools;
    private int totalNumOfPoolsPerVendor;
    private int maxTicketCapacity;

    @Autowired
    ManageTicketPool manageTicketPool;

    public ManageVendors(SystemConfiguration systemConfig) {
        this.systemConfig = systemConfig;
        this.systemConfig.addObserver(this);

        this.totalVendors = systemConfig.getTotalVendors();
        this.totalNumberOfPools = systemConfig.getTotalNumOfPools();
        this.totalNumOfPoolsPerVendor = systemConfig.getTotalNumOfPoolsPerVendor();
        this.maxTicketCapacity = systemConfig.getMaxTicketCapacity();
    }

    @Override
    public synchronized void updateConfiguration(SystemConfiguration config) {
        this.totalVendors = config.getTotalVendors();
        this.totalNumberOfPools = config.getTotalNumOfPools();
        this.totalNumOfPoolsPerVendor = config.getTotalNumOfPoolsPerVendor();
        this.maxTicketCapacity = config.getMaxTicketCapacity();
    }

    // Add a new vendor and start its thread
    public synchronized void createVendors(Vendor vendor) {
        if (vendorList.size() >= systemConfig.getTotalVendors()) {
            log.warn("Cannot add more vendors. Limit of [{}] reached.", totalVendors);
            return;
        }

        Vendor newVendor = new Vendor(vendor.getVendorName(), vendor.getVendorId());
        vendorList.put(newVendor.getVendorId(), newVendor);

        Thread vendorThread = new Thread(newVendor);
        vendorThreads.put(newVendor.getVendorId(), vendorThread);
        vendorThread.start();

        log.info("Vendor [{}] added and thread started.", newVendor.getVendorName());
    }

    // Assign an event creation task to the appropriate vendor
    public synchronized void createEvents(TicketPool ticketPoolEvent) {

        if (manageTicketPool.getTicketPoolArraySize() >= totalNumberOfPools) {
            log.warn("Cannot create more events. Limit of [{}] reached.", totalNumberOfPools);
            return;
        }

        ticketPoolEvent.setMaxTicketCapacity(maxTicketCapacity);
        TicketPool ticketPool = new TicketPool(ticketPoolEvent);
        Vendor vendor;
        synchronized (vendorList) {
            vendor = vendorList.get(ticketPool.getVendorId());
        }
        if (vendor != null) {

            if (vendor.getVendorEventListSize() >= totalNumOfPoolsPerVendor) {
                log.warn("Cannot create more events for vendor : {}. Limit of [{}] reached.", vendor.getVendorName(), totalNumOfPoolsPerVendor);
                return;
            }

            vendor.addTask(ticketPool); // Add the event creation task to the vendor's queue
            manageTicketPool.addTicketPool(ticketPool); //
            log.info("Task assigned to Vendor [{}] for event [{}]", vendor.getVendorName(), ticketPool.getTicketPoolName());

            resetTicketCounter();

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
            for (Map.Entry<String, TicketPool> ticketPoolEntry : vendor.getVendorEventList().entrySet()) {
                TicketPool pool = ticketPoolEntry.getValue();
                writer.write("    Event: " + pool.getEventId()
                        + " | Pool Name: " + pool.getTicketPoolName()
                        + " | Max Capacity: " + pool.getMaxTicketCapacity() + "\n");
            }
        }
    }

    /**
     * Handles vendor sign-in validation and response creation.
     *
     * @param vendorId   the ID of the vendor trying to sign in
     * @param vendorName the name of the vendor trying to sign in
     * @return ResponseEntity with the appropriate message and status
     */
    public ResponseEntity<String> signInVendor(String vendorId, String vendorName) {
        Vendor storedVendor = vendorList.get(vendorId);

        if (storedVendor == null) {
            // Vendor ID not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found.");
        }

        if (storedVendor.getVendorName().equals(vendorName)) {
            // Vendor ID and Name match
            return ResponseEntity.ok("Sign-in successful!");
        } else {
            // Vendor ID matches but Name does not
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vendor ID found, but name does not match.");
        }
    }
}
