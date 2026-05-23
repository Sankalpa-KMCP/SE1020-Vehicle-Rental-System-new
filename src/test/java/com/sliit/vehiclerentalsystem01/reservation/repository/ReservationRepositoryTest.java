package com.sliit.vehiclerentalsystem01.reservation.repository;

import com.sliit.vehiclerentalsystem01.reservation.entity.Booking;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ReservationRepositoryTest {

    @Test
    void findAllSkipsMalformedRowsAndKeepsValidBookings(@org.junit.jupiter.api.io.TempDir Path tempDir) throws Exception {
        ReservationRepository repository = new ReservationRepository();
        ReflectionTestUtils.setField(repository, "dataDirectory", tempDir.toString());

        Path file = tempDir.resolve("bookings.txt");
        Files.writeString(file, String.join(System.lineSeparator(),
                "# header",
                "TX-1,alice,2026-05-23,veh-1,3,CONFIRMED,ok",
                "bad,row"
        ), StandardCharsets.UTF_8);

        List<Booking> bookings = repository.findAll();

        assertEquals(1, bookings.size());
        Booking booking = bookings.get(0);
        assertEquals("TX-1", booking.getId());
        assertEquals("alice", booking.getCustomerName());
        assertEquals("veh-1", booking.getVehicleId());
        assertFalse(booking.getStatus().isBlank());
    }
}
