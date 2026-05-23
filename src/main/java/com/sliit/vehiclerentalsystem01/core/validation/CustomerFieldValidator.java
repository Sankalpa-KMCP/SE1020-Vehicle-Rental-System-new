package com.sliit.vehiclerentalsystem01.core.validation;

import com.sliit.vehiclerentalsystem01.core.exceptions.ValidationException;
import com.sliit.vehiclerentalsystem01.user.entity.Customer;

import java.util.regex.Pattern;

// Check customer phone, NIC, and license format
public final class CustomerFieldValidator {

    public static final Pattern PHONE = Pattern.compile("^[0-9]{10}$"); // 10 digit phone
    public static final Pattern NIC = Pattern.compile("^([0-9]{12}|[0-9]{9}[VXvx])$");
    public static final Pattern LICENSE = Pattern.compile("^[A-Za-z][0-9]{7}$");

    private CustomerFieldValidator() {
    }

    public static boolean isMissingDocument(String value) {
        String cleaned = value == null ? "" : value.trim();
        return cleaned.isEmpty() || "N/A".equalsIgnoreCase(cleaned);
    }

    public static void validate(Customer customer) {
        if (customer == null) {
            return;
        }
        String phone = customer.getPhoneNumber() == null ? "" : customer.getPhoneNumber().trim();
        if (!PHONE.matcher(phone).matches()) {
            throw new ValidationException("invalid_phone");
        }

        String nic = customer.getNic() == null ? "" : customer.getNic().trim();
        if (!isMissingDocument(nic) && !NIC.matcher(nic).matches()) {
            throw new ValidationException("invalid_nic");
        }

        String license = customer.getLicenseNumber() == null ? "" : customer.getLicenseNumber().trim();
        if (!isMissingDocument(license) && !LICENSE.matcher(license).matches()) {
            throw new ValidationException("invalid_license");
        }
    }
}
