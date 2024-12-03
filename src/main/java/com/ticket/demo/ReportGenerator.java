package com.ticket.demo;

import com.ticket.demo.core.pools.ManageTicketPool;
import com.ticket.demo.core.pools.ManageConsumers;
import com.ticket.demo.core.pools.ManageVendors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Component
public class ReportGenerator {
    private int id = 1;

    @Autowired
    ManageVendors manageVendors;

    @Autowired
    ManageTicketPool manageTicketPool;

    @Autowired
    ManageConsumers manageConsumers;

    public void generateReport() {
        String fileName = "report"+id;
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write Vendor Details
            manageTicketPool.writeTicketPools(writer);
            manageVendors.writeVendors(writer);
            manageConsumers.writeConsumers(writer);

        } catch (IOException e) {
            log.warn("Error generating report: " + e.getMessage());
            e.printStackTrace();
        }

        id++;
    }
}
