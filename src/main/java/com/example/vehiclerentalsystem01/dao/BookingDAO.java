package com.example.vehiclerentalsystem01.dao;

import com.example.vehiclerentalsystem01.models.Booking;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data Access Object for Bookings.
 * Handles all File I/O operations for the bookings.txt file.
 */
@Repository
public class BookingDAO {
    private static final String FOLDER_PATH = System.getProperty("user.home") + File.separator + "RentalData";
    private static final String FILE_PATH = FOLDER_PATH + File.separator + "bookings.txt";

    public BookingDAO() {
        // Ensure the directory exists
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) folder.mkdirs();
    }

    private String normalized(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean hasStatus(Booking booking, String expectedStatus) {
        return booking != null && expectedStatus.equalsIgnoreCase(normalized(booking.getStatus()));
    }

    private String csvSafe(String value) {
        if (value == null) return "";
        return value.replace(",", " ").replace("\r", " ").replace("\n", " ").trim();
    }

    // CRUD: CREATE
    public void saveBooking(Booking booking) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(booking.getTransactionId() + "," + booking.getCustomerName() + "," +
                    booking.getRentalDate() + "," + booking.getVehicleId() + "," +
                    booking.getDurationDays() + "," + booking.getStatus() + "," + csvSafe(booking.getStaffMessage()));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CRUD: READ ALL
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return bookings;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 6) {
                    String staffMessage = data.length >= 7 ? data[6] : "";
                    bookings.add(new Booking(
                            data[0],
                            data[1],
                            data[2],
                            data[3],
                            Integer.parseInt(data[4]),
                            data[5],
                            staffMessage
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    // CRUD: READ BY ID (Fixes "cannot find symbol" error)
    public Booking getBookingById(String id) {
        for (Booking b : getAllBookings()) {
            if (b.getTransactionId().equals(id)) {
                return b;
            }
        }
        return null;
    }

    // CRUD: UPDATE (Fixes "cannot find symbol" error)
    public void updateBooking(Booking updatedBooking) {
        List<Booking> bookings = getAllBookings();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Booking b : bookings) {
                if (b.getTransactionId().equals(updatedBooking.getTransactionId())) {
                    // Write the new data
                    writer.write(updatedBooking.getTransactionId() + "," + updatedBooking.getCustomerName() + "," +
                            updatedBooking.getRentalDate() + "," + updatedBooking.getVehicleId() + "," +
                            updatedBooking.getDurationDays() + "," + updatedBooking.getStatus() + "," + csvSafe(updatedBooking.getStaffMessage()));
                } else {
                    // Keep existing data
                    writer.write(b.getTransactionId() + "," + b.getCustomerName() + "," +
                            b.getRentalDate() + "," + b.getVehicleId() + "," +
                            b.getDurationDays() + "," + b.getStatus() + "," + csvSafe(b.getStaffMessage()));
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CRUD: DELETE (Fixes "cannot find symbol" error)
    public void deleteBooking(String id) {
        List<Booking> bookings = getAllBookings();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Booking b : bookings) {
                // If ID does NOT match, write it back. This effectively removes the target ID.
                if (!b.getTransactionId().equals(id)) {
                    writer.write(b.getTransactionId() + "," + b.getCustomerName() + "," +
                            b.getRentalDate() + "," + b.getVehicleId() + "," +
                            b.getDurationDays() + "," + b.getStatus() + "," + csvSafe(b.getStaffMessage()));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Backward compatibility helper for simple status updates.
     */
    public void updateBookingStatus(String id, String newStatus) {
        List<Booking> bookings = getAllBookings();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Booking b : bookings) {
                if (b.getTransactionId().equals(id)) {
                    b.setStatus(newStatus);
                }
                writer.write(b.getTransactionId() + "," + b.getCustomerName() + "," +
                        b.getRentalDate() + "," + b.getVehicleId() + "," +
                        b.getDurationDays() + "," + b.getStatus() + "," + csvSafe(b.getStaffMessage()));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPaymentAllowed(String bookingId) {
        Booking booking = getBookingById(bookingId);
        return hasStatus(booking, "CONFIRMED");
    }

    public boolean hasEligibleReviewBooking(String customerName) {
        String normalizedCustomer = normalized(customerName);
        if (normalizedCustomer.isEmpty()) {
            return false;
        }

        for (Booking booking : getAllBookings()) {
            if (normalized(booking.getCustomerName()).equalsIgnoreCase(normalizedCustomer)
                    && (hasStatus(booking, "CONFIRMED") || hasStatus(booking, "COMPLETED"))) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getUnavailableVehicleIds() {
        Set<String> unavailableVehicleIds = new HashSet<>();
        for (Booking booking : getAllBookings()) {
            if (hasStatus(booking, "CONFIRMED") || hasStatus(booking, "CANCELLATION_REQUESTED") || hasStatus(booking, "UPDATED")) {
                String vehicleId = normalized(booking.getVehicleId());
                if (!vehicleId.isEmpty()) {
                    unavailableVehicleIds.add(vehicleId);
                }
            }
        }
        return unavailableVehicleIds;
    }

    public boolean isVehicleAvailable(String vehicleId) {
        String normalizedVehicleId = normalized(vehicleId);
        if (normalizedVehicleId.isEmpty()) {
            return false;
        }
        return !getUnavailableVehicleIds().contains(normalizedVehicleId);
    }

    public boolean hasDateOverlap(Booking booking, LocalDate requestedStart, LocalDate requestedEnd) {
        if (booking == null || requestedStart == null || requestedEnd == null) {
            return false;
        }

        try {
            LocalDate bookingStart = LocalDate.parse(normalized(booking.getRentalDate()));
            LocalDate bookingEnd = bookingStart.plusDays(Math.max(1, booking.getDurationDays()) - 1L);
            return !bookingEnd.isBefore(requestedStart) && !bookingStart.isAfter(requestedEnd);
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
