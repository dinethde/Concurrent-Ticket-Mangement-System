package com.ticket.demo.controller;

import com.ticket.demo.core.pools.ManageVendors;
import com.ticket.demo.core.pools.TicketPool;
import com.ticket.demo.core.Vendor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@CrossOrigin
public class VendorController {

    @Autowired
    ManageVendors manageVendors;

    @PostMapping("/vendor")
    public void  createVendor(@RequestBody Vendor vendor) {
        manageVendors.createVendors(vendor);
    }

    @PostMapping("/event")
    public void createEvent(@RequestBody TicketPool event) {
        manageVendors.createEvents(event);
    }

    @GetMapping("/login")
    public ResponseEntity<String> signInVendor(
            @RequestParam String vendorId,
            @RequestParam String vendorName) {
        log.info("Sign in");
        // Validate the query parameters
        if (vendorId.isBlank() || vendorName.isBlank()) {
            return ResponseEntity.badRequest().body("Vendor ID and Name must be provided.");
        }
        // Delegate sign-in validation to ManageVendors
        return manageVendors.signInVendor(vendorId, vendorName);
    }
}
