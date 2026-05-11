package com.example.vehiclerentalsystem01.services;

import com.example.vehiclerentalsystem01.exceptions.AuthenticationException;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    public static final String ADMIN_LOGGED_IN = "adminLoggedIn";
    public static final String ADMIN_USER = "adminUser";
    public static final String USERNAME = "username";
    public static final String NAME = "name";
    public static final String IS_MASTER_ADMIN = "isMasterAdmin";
    public static final String CUSTOMER_LOGGED_IN = "customerLoggedIn";
    public static final String CUSTOMER_ID = "customerId";
    public static final String CUSTOMER_USERNAME = "customerUsername";

    public void createAdminSession(HttpSession session, String username, String name, boolean isMasterAdmin) {
        session.setAttribute(ADMIN_LOGGED_IN, true);
        session.setAttribute(ADMIN_USER, username);
        session.setAttribute(USERNAME, username);
        session.setAttribute(NAME, name);
        session.setAttribute(IS_MASTER_ADMIN, isMasterAdmin);
        logger.info("Admin session created for user: {}", username);
    }

    public void createCustomerSession(HttpSession session, String customerId, String username) {
        session.setAttribute(CUSTOMER_LOGGED_IN, true);
        session.setAttribute(CUSTOMER_ID, customerId);
        session.setAttribute(CUSTOMER_USERNAME, username);
        logger.info("Customer session created for user: {}", username);
    }

    public boolean isAdminLoggedIn(HttpSession session) {
        Boolean loggedIn = (Boolean) session.getAttribute(ADMIN_LOGGED_IN);
        return Boolean.TRUE.equals(loggedIn);
    }

    public boolean isCustomerLoggedIn(HttpSession session) {
        Boolean loggedIn = (Boolean) session.getAttribute(CUSTOMER_LOGGED_IN);
        return Boolean.TRUE.equals(loggedIn);
    }

    public boolean isMasterAdmin(HttpSession session) {
        Boolean isMaster = (Boolean) session.getAttribute(IS_MASTER_ADMIN);
        return Boolean.TRUE.equals(isMaster);
    }

    public String getAdminUsername(HttpSession session) {
        return (String) session.getAttribute(ADMIN_USER);
    }

    public String getCustomerUsername(HttpSession session) {
        return (String) session.getAttribute(CUSTOMER_USERNAME);
    }

    public String getCustomerId(HttpSession session) {
        return (String) session.getAttribute(CUSTOMER_ID);
    }

    public void invalidateSession(HttpSession session) {
        String username = getAdminUsername(session);
        if (username == null) {
            username = getCustomerUsername(session);
        }
        session.invalidate();
        logger.info("Session invalidated for user: {}", username);
    }

    public void validateAdminSession(HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            throw new AuthenticationException("Admin access required. Please login.");
        }
    }

    public void validateCustomerSession(HttpSession session) {
        if (!isCustomerLoggedIn(session)) {
            throw new AuthenticationException("Customer access required. Please login.");
        }
    }

    public void validateAnySession(HttpSession session) {
        if (!isAdminLoggedIn(session) && !isCustomerLoggedIn(session)) {
            throw new AuthenticationException("Access denied. Please login.");
        }
    }

    public void refreshSession(HttpSession session) {
        session.setMaxInactiveInterval(30 * 60); // 30 minutes
        logger.debug("Session refreshed");
    }
}
