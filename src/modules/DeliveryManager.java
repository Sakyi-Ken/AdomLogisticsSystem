package modules;

import java.util.Scanner;
import models.Delivery;
import structures.list.LinkedList;
import utils.FileHandler;
import utils.InputValidator;

public class DeliveryManager {

    private final LinkedList<Delivery> deliveryQueue = new LinkedList<>();
    private final Scanner scanner = new Scanner(System.in);
    private final DriverManager driverManager;
    private final VehicleManager vehicleManager;

    public DeliveryManager(DriverManager driverManager, VehicleManager vehicleManager) {
        this.driverManager = driverManager;
        this.vehicleManager = vehicleManager;

        var loaded = FileHandler.loadDeliveries();
        for (Delivery d : loaded) {
            deliveryQueue.add(d);
        }

        if (!loaded.isEmpty()) {
            System.out.println("Loaded " + loaded.size() + " deliveries from file.");
        }
    }

    public void addDelivery() {
        System.out.println("\n--- Schedule New Delivery ---");

        while (true) {
            // Get package ID
            String packageId = InputValidator.getValidString("Enter Package ID: ", 3, 20);
            if (packageId.equals("BACK")) {
                return;
            }

            // Check if package ID already exists
            Delivery existing = deliveryQueue.find(d -> d.getPackageId().equals(packageId));
            if (existing != null) {
                if (!InputValidator.handleErrorAndAskRetry("Package ID already exists!")) {
                    return;
                }
                continue;
            }

            // Get origin with location validation
            String origin = InputValidator.getValidLocation("Enter Origin: ", 2, 50);
            if (origin.equals("BACK")) {
                return;
            }

            // Get destination with location validation  
            String destination = InputValidator.getValidLocation("Enter Destination: ", 2, 50);
            if (destination.equals("BACK")) {
                return;
            }

            // Validate that origin and destination are different
            if (origin.equalsIgnoreCase(destination)) {
                if (!InputValidator.handleErrorAndAskRetry("Origin and destination cannot be the same!")) {
                    return;
                }
                continue;
            }

            // Get ETA with proper future date validation
            String eta = InputValidator.getValidFutureDateTime("Enter ETA");
            if (eta.equals("BACK")) {
                return;
            }

            try {
                var driver = driverManager.assignDriver();
                if (driver == null) {
                    if (!InputValidator.handleErrorAndAskRetry("No available driver. Cannot schedule delivery.")) {
                        return;
                    }
                    continue;
                }

                var vehicle = vehicleManager.getAvailableVehicle();
                if (vehicle == null) {
                    if (!InputValidator.handleErrorAndAskRetry("No available vehicle. Cannot schedule delivery.")) {
                        return;
                    }
                    continue;
                }

                Delivery delivery = new Delivery(
                        packageId, origin, destination, eta,
                        vehicle.getRegistrationNumber(),
                        driver.getDriverId(),
                        "Pending"
                );

                deliveryQueue.add(delivery);
                FileHandler.saveDeliveries(deliveryQueue.toList());

                InputValidator.showSuccess("Delivery scheduled!");
                return;

            } catch (Exception e) {
                if (!InputValidator.handleErrorAndAskRetry("Error creating delivery: " + e.getMessage())) {
                    return;
                }
            }
        }
    }

    public void listDeliveries() {
        System.out.println("\n--- All Deliveries ---");
        deliveryQueue.printAll();
    }

    public void updateDeliveryStatus() {
        System.out.println("\n--- Update Delivery Status ---");

        while (true) {
            String packageId = InputValidator.getValidString("Enter Package ID to update status: ", 1, 20);
            if (packageId.equals("BACK")) {
                return;
            }

            var delivery = deliveryQueue.find(d -> d.getPackageId().equalsIgnoreCase(packageId));
            if (delivery == null) {
                if (!InputValidator.handleErrorAndAskRetry("Delivery not found.")) {
                    return;
                }
                continue;
            }

            String[] allowedStatuses = {"Pending", "In Transit", "Delivered", "Cancelled"};
            String status = InputValidator.getValidChoice("Enter new status (Pending/In Transit/Delivered/Cancelled): ", allowedStatuses, false);
            if (status.equals("BACK")) {
                return;
            }

            try {
                delivery.setStatus(status);
                FileHandler.saveDeliveries(deliveryQueue.toList());
                InputValidator.showSuccess("Delivery status updated.");
                return;
            } catch (Exception e) {
                if (!InputValidator.handleErrorAndAskRetry("Error updating delivery status: " + e.getMessage())) {
                    return;
                }
            }
        }
    }

    public void rerouteDelivery() {
        System.out.println("\n--- Reroute Delivery ---");

        while (true) {
            String packageId = InputValidator.getValidString("Enter Package ID to reroute: ", 1, 20);
            if (packageId.equals("BACK")) {
                return;
            }

            var delivery = deliveryQueue.find(d -> d.getPackageId().equalsIgnoreCase(packageId));
            if (delivery == null) {
                if (!InputValidator.handleErrorAndAskRetry("Delivery not found.")) {
                    return;
                }
                continue;
            }

            // Show current destination
            System.out.println("Current destination: " + delivery.getDestination());
            String currentDestination = delivery.getDestination(); // Store current destination

            String newDest = InputValidator.getValidLocation("Enter new destination: ", 2, 50);
            if (newDest.equals("BACK")) {
                return;
            }

            // Validate that new destination is different from current
            if (newDest.equalsIgnoreCase(delivery.getDestination())) {
                if (!InputValidator.handleErrorAndAskRetry("New destination must be different from current destination!")) {
                    return;
                }
                continue;
            }

            // Validate that new destination is different from origin
            if (newDest.equalsIgnoreCase(delivery.getOrigin())) {
                if (!InputValidator.handleErrorAndAskRetry("Destination cannot be the same as origin!")) {
                    return;
                }
                continue;
            }

            try {
                delivery.setDestination(newDest);
                FileHandler.saveDeliveries(deliveryQueue.toList());
                InputValidator.showSuccess("Delivery rerouted from '" + currentDestination + "' to '" + newDest + "'");
                return;
            } catch (Exception e) {
                if (!InputValidator.handleErrorAndAskRetry("Error rerouting delivery: " + e.getMessage())) {
                    return;
                }
            }
        }
    }

    public void removeDelivery() {
        System.out.print("Enter Package ID to remove: ");
        String packageId = scanner.nextLine().trim();

        boolean removed = deliveryQueue.removeIf(d -> d.getPackageId().equalsIgnoreCase(packageId));
        if (removed) {
            FileHandler.saveDeliveries(deliveryQueue.toList());
            System.out.println("Delivery removed.");
        } else {
            System.out.println("Package not found.");
        }
    }

    // Optional: Search deliveries by Package ID
    public void searchByPackageId() {
        System.out.println("\n--- Search Delivery by Package ID ---");

        while (true) {
            String packageId = InputValidator.getValidString("Enter Package ID to search: ", 1, 20);
            if (packageId.equals("BACK")) {
                return;
            }

            var delivery = deliveryQueue.find(d -> d.getPackageId().equalsIgnoreCase(packageId));
            if (delivery != null) {
                System.out.println("Delivery Found:");
                System.out.println(delivery);
                return;
            } else {
                if (!InputValidator.handleErrorAndAskRetry("Delivery not found.")) {
                    return;
                }
            }
        }
    }

    /**
     * View all active deliveries (excludes cancelled deliveries by default)
     */
    public void viewActiveDeliveries() {
        System.out.println("\n--- Active Deliveries ---");

        boolean foundActive = false;
        for (var delivery : deliveryQueue.toList()) {
            if (!delivery.getStatus().equalsIgnoreCase("Cancelled")) {
                System.out.println(delivery);
                System.out.println("----------------------------------------");
                foundActive = true;
            }
        }

        if (!foundActive) {
            System.out.println("No active deliveries found.");
        }
    }

    /**
     * View all deliveries including cancelled ones
     */
    public void viewAllDeliveries() {
        System.out.println("\n--- All Deliveries (Including Cancelled) ---");

        if (deliveryQueue.toList().isEmpty()) {
            System.out.println("No deliveries found.");
            return;
        }

        int active = 0, cancelled = 0;
        for (var delivery : deliveryQueue.toList()) {
            System.out.println(delivery);
            System.out.println("----------------------------------------");

            if (delivery.getStatus().equalsIgnoreCase("Cancelled")) {
                cancelled++;
            } else {
                active++;
            }
        }

        System.out.println("\nSummary: " + active + " active deliveries, " + cancelled + " cancelled deliveries");
    }

    /**
     * View deliveries with filter options
     */
    public void viewDeliveriesMenu() {
        while (true) {
            System.out.println("\n--- View Deliveries ---");
            System.out.println("1. View Active Deliveries (recommended)");
            System.out.println("2. View All Deliveries (including cancelled)");
            System.out.println("3. View by Status");

            int choice = InputValidator.getValidMenuChoice("Enter choice (1-3): ", 1, 3);
            if (choice == -1) {
                return;
            }

            switch (choice) {
                case 1 -> {
                    viewActiveDeliveries();
                    return;
                }
                case 2 -> {
                    viewAllDeliveries();
                    return;
                }
                case 3 -> {
                    viewDeliveriesByStatus();
                    return;
                }
            }
        }
    }

    /**
     * View deliveries filtered by status
     */
    public void viewDeliveriesByStatus() {
        String[] statuses = {"Pending", "In Transit", "Delivered", "Cancelled"};
        System.out.println("\nSelect status to filter:");
        for (int i = 0; i < statuses.length; i++) {
            System.out.println((i + 1) + ". " + statuses[i]);
        }

        int choice = InputValidator.getValidMenuChoice("Enter choice (1-" + statuses.length + "): ", 1, statuses.length);
        if (choice == -1) {
            return;
        }

        String selectedStatus = statuses[choice - 1];
        System.out.println("\n--- Deliveries with Status: " + selectedStatus + " ---");

        boolean found = false;
        for (var delivery : deliveryQueue.toList()) {
            if (delivery.getStatus().equalsIgnoreCase(selectedStatus)) {
                System.out.println(delivery);
                System.out.println("----------------------------------------");
                found = true;
            }
        }

        if (!found) {
            System.out.println("No deliveries found with status: " + selectedStatus);
        }
    }
}
