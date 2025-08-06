package models;

public class Delivery {

    private final String packageId;
    private final String origin;
    private String destination;
    private final String eta;
    private final String vehicleRegNo;
    private final String driverId;
    private String status; // e.g., Pending, In Transit, Delivered, Cancelled

    public Delivery(String packageId, String origin, String destination, String eta,
            String vehicleRegNo, String driverId, String status) {
        this.packageId = packageId;
        this.origin = origin;
        this.destination = destination;
        this.eta = eta;
        this.vehicleRegNo = vehicleRegNo;
        this.driverId = driverId;
        this.status = status;
    }

    // Getters
    public String getPackageId() {
        return packageId;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getEta() {
        return eta;
    }

    public String getVehicleRegNo() {
        return vehicleRegNo;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "Package ID: " + packageId
                + "\nOrigin: " + origin
                + "\nDestination: " + destination
                + "\nETA: " + eta
                + "\nVehicle: " + vehicleRegNo
                + "\nDriver: " + driverId
                + "\nStatus: " + status + "\n";
    }
}
