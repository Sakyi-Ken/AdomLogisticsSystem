package modules;

import java.util.ArrayList;
import java.util.List;
import models.Maintenance;
import models.Vehicle;
import utils.FileHandler;
import utils.InputValidator;

public class MaintenanceManager {

    private final List<Maintenance> allMaintenanceRecords = FileHandler.loadMaintenance(); // flat list
    private final List<Vehicle> sortedVehicleList = new ArrayList<>();
    // private final Scanner scanner = new Scanner(System.in);

    public MaintenanceManager(VehicleManager vehicleManager) {
        // Load vehicles and sort manually by mileage using QuickSort
        List<Vehicle> allVehicles = vehicleManager.getAllVehicles();
        sortedVehicleList.addAll(allVehicles);
        quickSortVehiclesByMileage(0, sortedVehicleList.size() - 1);
        System.out.println("Loaded " + allMaintenanceRecords.size() + " maintenance records.");
    }

    // QuickSort implementation for vehicles by mileage ascending
    private void quickSortVehiclesByMileage(int low, int high) {
        if (low < high) {
            int pivotIndex = partition(low, high);
            quickSortVehiclesByMileage(low, pivotIndex - 1);
            quickSortVehiclesByMileage(pivotIndex + 1, high);
        }
    }

    private int partition(int low, int high) {
        Vehicle pivot = sortedVehicleList.get(high);
        int pivotMileage = pivot.getMileage();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (sortedVehicleList.get(j).getMileage() <= pivotMileage) {
                i++;
                swapVehicles(i, j);
            }
        }
        swapVehicles(i + 1, high);
        return i + 1;
    }

    private void swapVehicles(int i, int j) {
        Vehicle temp = sortedVehicleList.get(i);
        sortedVehicleList.set(i, sortedVehicleList.get(j));
        sortedVehicleList.set(j, temp);
    }

    // === CORE LOGIC ===
    public void scheduleNextMaintenance() {
        if (sortedVehicleList.isEmpty()) {
            System.out.println("No vehicles currently need maintenance.");
            return;
        }

        Vehicle next = sortedVehicleList.remove(0); // simulate priority
        System.out.println("\nVehicle Due for Maintenance:");
        System.out.println(next);

        while (true) {
            // Get service type with validation
            String[] serviceTypes = {"Oil Change", "Tire Replacement", "Brake Service", "Engine Repair", "Regular Service", "Other"};
            System.out.println("\nSelect Service Type:");
            for (int i = 0; i < serviceTypes.length; i++) {
                System.out.println((i + 1) + ". " + serviceTypes[i]);
            }

            int serviceChoice = InputValidator.getValidMenuChoice("Enter choice (1-" + serviceTypes.length + "): ", 1, serviceTypes.length);
            if (serviceChoice == -1) {
                return;
            }

            String type = serviceTypes[serviceChoice - 1];
            if (type.equals("Other")) {
                type = InputValidator.getValidString("Enter custom service type: ", 2, 50);
                if (type.equals("BACK")) {
                    return;
                }
            }

            // Get service date with validation (past or current date for completed maintenance)
            String date = InputValidator.getValidPastDate("Enter Service Date");
            if (date.equals("BACK")) {
                return;
            }

            // Get parts replaced with validation
            String parts = InputValidator.getValidString("Enter Parts Replaced (or 'None'): ", 1, 100);
            if (parts.equals("BACK")) {
                return;
            }

            // Get cost with validation
            double cost = InputValidator.getValidCost("Enter Cost: $");
            if (cost == -999.0) {
                return;
            }

            // Get next scheduled service date (optional) - must be future date
            System.out.println("Next Scheduled Service Date (optional):");
            System.out.println("1. Enter future date");
            System.out.println("2. Skip");

            int dateChoice = InputValidator.getValidMenuChoice("Enter choice (1-2): ", 1, 2);
            if (dateChoice == -1) {
                return;
            }

            String nextServiceDate = "";
            if (dateChoice == 1) {
                nextServiceDate = InputValidator.getValidFutureDate("Enter Next Scheduled Service Date");
                if (nextServiceDate.equals("BACK")) {
                    return;
                }
            }

            try {
                Maintenance m = new Maintenance(
                        next.getRegistrationNumber(),
                        type, date, next.getMileage(),
                        parts, cost, nextServiceDate
                );

                allMaintenanceRecords.add(m);
                FileHandler.saveMaintenance(allMaintenanceRecords);
                InputValidator.showSuccess("Maintenance scheduled and logged successfully!");
                return;

            } catch (Exception e) {
                if (!InputValidator.handleErrorAndAskRetry("Error scheduling maintenance: " + e.getMessage())) {
                    return;
                }
            }
        }
    }

    public void viewHistory() {
        System.out.println("\n--- View Maintenance History ---");

        while (true) {
            String regNo = InputValidator.getValidString("Enter Vehicle Reg No: ", 1, 20);
            if (regNo.equals("BACK")) {
                return;
            }

            boolean found = false;
            System.out.println("\n--- Maintenance History for " + regNo + " ---");
            for (Maintenance m : allMaintenanceRecords) {
                if (m.getRegNo().equalsIgnoreCase(regNo)) {
                    System.out.println(m);
                    found = true;
                }
            }

            if (!found) {
                if (!InputValidator.handleErrorAndAskRetry("No maintenance records found for this vehicle.")) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    public void showMaintenanceQueue() {
        System.out.println("\n--- Maintenance Priority Queue (by mileage) ---");
        for (Vehicle v : sortedVehicleList) {
            System.out.printf("• RegNo: %s | Mileage: %d km\n", v.getRegistrationNumber(), v.getMileage());
        }
    }

    // === ALIASES FOR MainMenu.java compatibility ===
    public void scheduleMaintenance() {
        scheduleNextMaintenance();
    }

    public void viewMaintenanceHistory() {
        viewHistory();
    }

    public void viewNextVehicleDue() {
        if (sortedVehicleList.isEmpty()) {
            System.out.println("No vehicles currently due for maintenance.");
        } else {
            Vehicle next = sortedVehicleList.get(0);
            System.out.println("\nNext Vehicle Due for Maintenance:");
            System.out.printf("• RegNo: %s | Mileage: %d km\n", next.getRegistrationNumber(), next.getMileage());
        }
    }
}
