package models;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {

    private final String registrationNumber;
    private final String type;           // e.g., "Truck" or "Van"
    private int mileage;           // in kilometers
    private double fuelUsage;      // in liters per 100km
    private String driverId;       // ID of the assigned driver
    private final List<String> maintenanceHistory;

    // Constructor
    public Vehicle(String registrationNumber, String type, int mileage, double fuelUsage, String driverId) {
        this.registrationNumber = registrationNumber;
        this.type = type;
        this.mileage = mileage;
        this.fuelUsage = fuelUsage;
        this.driverId = driverId;
        this.maintenanceHistory = new ArrayList<>();
    }

    // Getters and Setters
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getType() {
        return type;
    }

    public int getMileage() {
        return mileage;
    }

    public double getFuelUsage() {
        return fuelUsage;
    }

    public String getDriverId() {
        return driverId;
    }

    public List<String> getMaintenanceHistory() {
        return maintenanceHistory;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public void setFuelUsage(double fuelUsage) {
        this.fuelUsage = fuelUsage;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public void addMaintenanceRecord(String record) {
        maintenanceHistory.add(record);
    }

    // String representation
    @Override
    public String toString() {
        return "Vehicle [Reg No: " + registrationNumber
                + ", Type: " + type
                + ", Mileage: " + mileage + " km"
                + ", Fuel Usage: " + fuelUsage + " L/100km"
                + ", Driver ID: " + driverId + "]";
    }
}
