package com.ticket.demo.configuration;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SystemConfiguration {
    private int totalTickets = 1000; // Default total tickets
    private int totalNumOfPools = 10;
    private int totalNumOfPoolsPerVendor = 2;
    private int maxTicketCapacity = totalTickets/totalNumOfPools; // Default max capacity per pool
    private int totalVendors = 3; // Default vendor limit
    private int totalConsumers = 3; // Default consumer limit

    private final List<ConfigObserver> observers = new ArrayList<>();

    // Register a manager as an observer
    public void addObserver(ConfigObserver observer) {
        observers.add(observer);
    }

    // Notify all observers when a config changes
    private void notifyObservers() {
        for (ConfigObserver observer : observers) {
            observer.updateConfiguration(this);
        }
    }

    // Admin-facing methods to update configuration
    public synchronized void updateTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
        notifyObservers();
    }

    public synchronized void updateMaxTicketCapacity(int maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
        notifyObservers();
    }

    public synchronized void updateTotalVendors(int totalVendors) {
        this.totalVendors = totalVendors;
        notifyObservers();
    }

    public synchronized void updateTotalConsumers(int totalConsumers) {
        this.totalConsumers = totalConsumers;
        notifyObservers();
    }

    public synchronized void totalNumOfPools(int totalNumOfPools) {
        this.totalNumOfPools = totalNumOfPools;
        notifyObservers();
    }

    public synchronized void totalNumOfPoolsPerVendor(int totalNumOfPoolsPerVendor) {
        this.totalNumOfPoolsPerVendor = totalNumOfPoolsPerVendor;
        notifyObservers();
    }

}