package com.ticket.demo.controller;

import com.ticket.demo.core.pools.ManageVendors;
import com.ticket.demo.core.pools.TicketPool;
import com.ticket.demo.core.Vendor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class VendorController {

    @Autowired
    ManageVendors manageVendors;

    @PostMapping("/vendor")
    public void createVendor(@RequestBody Vendor vendor) {
        manageVendors.createVendors(vendor);
    }

    @PostMapping("/event")
    public void createEvent(@RequestBody TicketPool event) {
        manageVendors.createEvents(event);
    }
}
