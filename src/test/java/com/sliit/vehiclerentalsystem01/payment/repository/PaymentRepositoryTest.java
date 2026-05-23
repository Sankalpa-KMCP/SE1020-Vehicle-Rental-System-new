package com.sliit.vehiclerentalsystem01.payment.repository;

import com.sliit.vehiclerentalsystem01.payment.entity.CardPayment;
import com.sliit.vehiclerentalsystem01.payment.entity.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentRepositoryTest {

    @Test
    void findAllSkipsMalformedRowsAndKeepsValidPayments(@org.junit.jupiter.api.io.TempDir Path tempDir) throws Exception {
        PaymentRepository repository = new PaymentRepository();
        ReflectionTestUtils.setField(repository, "dataDirectory", tempDir.toString());

        Path file = tempDir.resolve("payments.txt");
        Files.writeString(file, String.join(System.lineSeparator(),
                "# header",
                "CARD,PAY-1,BOOK-1,125.50,PAID",
                "BROKEN,ROW"
        ), StandardCharsets.UTF_8);

        List<Payment> payments = repository.findAll();

        assertEquals(1, payments.size());
        Payment payment = payments.get(0);
        assertInstanceOf(CardPayment.class, payment);
        assertEquals("PAY-1", payment.getId());
        assertEquals("BOOK-1", payment.getBookingId());
        assertEquals(125.50, payment.getAmount());
        assertTrue(payment.getStatus().equalsIgnoreCase("PAID"));
    }
}
