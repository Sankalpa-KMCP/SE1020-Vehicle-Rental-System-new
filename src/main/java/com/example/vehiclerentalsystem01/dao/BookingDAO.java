package com.example.vehiclerentalsystem01.dao;

import com.example.vehiclerentalsystem01.models.Booking;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Bookings.
 * Handles all File I/O operations for the bookings.txt file.
 */
public class BookingDAO {
    private static final String FOLDER_PATH = System.getProperty("user.home") + File.separator + "RentalData";
    private static final String FILE_PATH = FOLDER_PATH + File.separator + "bookings.txt";

    public BookingDAO() {
        // Ensure the directory exists
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) folder.mkdirs();
    }

    // CRUD: CREATE
    public void saveBooking(Booking booking) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(booking.getTransactionId() + "," + booking.getCustomerName() + "," +
                    booking.getRentalDate() + "," + booking.getVehicleId() + "," +
                    booking.getDurationDays() + "," + booking.getStatus());
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
                String[] data = line.split(",");
                if (data.length == 6) {
                    bookings.add(new Booking(
                            data[0], // ID
                            data[1], // Name
                            data[2], // Date
                            data[3], // VehicleID
                            Integer.parseInt(data[4]), // Days
                            data[5]  // Status
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
                            updatedBooking.getDurationDays() + "," + updatedBooking.getStatus());
                } else {
                    // Keep existing data
                    writer.write(b.getTransactionId() + "," + b.getCustomerName() + "," +
                            b.getRentalDate() + "," + b.getVehicleId() + "," +
                            b.getDurationDays() + "," + b.getStatus());
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
                            b.getDurationDays() + "," + b.getStatus());
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
                        b.getDurationDays() + "," + b.getStatus());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}