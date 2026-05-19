package com.sliit.vehiclerentalsystem01.maintenance.entity;

public final class MaintenanceStatus {

    public static final String AVAILABLE = "AVAILABLE";

    public static final String IN_USE = "IN_USE";

    public static final String MAINTENANCE = "MAINTENANCE";

    private MaintenanceStatus() {
    }

    public static boolean isAvailable(String status) {
        return AVAILABLE.equals(status);
    }

    public static boolean isInUse(String status) {
        return IN_USE.equals(status);
    }

    public static boolean isMaintenance(String status) {
        return MAINTENANCE.equals(status);
    }

    public static boolean isValidStatus(String status) {
        return isAvailable(status)
                || isInUse(status)
                || isMaintenance(status);
    }
}