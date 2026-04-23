package com.example.vehiclerentalsystem01.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/support")
public class SupportController extends HttpServlet {
    private static final String FOLDER_PATH = System.getProperty("user.home") + File.separator + "RentalData";
    private static final String FILE_PATH = FOLDER_PATH + File.separator + "support_tickets.txt";

    public SupportController() {
        File folder = new File(FOLDER_PATH);
        if (!folder.exists() && !folder.mkdirs()) {
            System.err.println("Could not create support data folder: " + folder.getAbsolutePath());
        }
    }

    private String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().replace("\r", " ").replace("\n", " ").replace("|", "/");
    }

    private String normalized(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isAdmin(HttpSession session) {
        return session != null && Boolean.TRUE.equals(session.getAttribute("adminLoggedIn"));
    }

    private List<SupportTicket> readAllTickets() {
        List<SupportTicket> tickets = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return tickets;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|", -1);
                if (data.length >= 4) {
                    SupportTicket ticket = new SupportTicket();
                    ticket.id = data[0];
                    ticket.username = data[1];
                    ticket.subject = data[2];
                    ticket.message = data[3];
                    ticket.status = data.length >= 5 && !normalized(data[4]).isEmpty() ? data[4] : "OPEN";
                    ticket.resolvedBy = data.length >= 6 ? data[5] : "";
                    ticket.resolvedAt = data.length >= 7 ? data[6] : "";
                    ticket.resolutionNote = data.length >= 8 ? data[7] : "";
                    tickets.add(ticket);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load support tickets: " + e.getMessage());
        }
        return tickets;
    }

    private void writeAllTickets(List<SupportTicket> tickets) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (SupportTicket ticket : tickets) {
                writer.write(sanitize(ticket.id) + "|"
                        + sanitize(ticket.username) + "|"
                        + sanitize(ticket.subject) + "|"
                        + sanitize(ticket.message) + "|"
                        + sanitize(ticket.status) + "|"
                        + sanitize(ticket.resolvedBy) + "|"
                        + sanitize(ticket.resolvedAt) + "|"
                        + sanitize(ticket.resolutionNote));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save support tickets: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String action = request.getParameter("action");
        if ("staff_list".equals(action)) {
            HttpSession session = request.getSession(false);
            if (!isAdmin(session)) {
                response.sendRedirect(request.getContextPath() + "/admin-login.jsp");
                return;
            }

            request.setAttribute("tickets", readAllTickets());
            request.getRequestDispatcher("/support-dashboard.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/customer-support.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        if ("submit_ticket".equals(action)) {
            String username = sanitize(request.getParameter("username"));
            String subject = sanitize(request.getParameter("subject"));
            String message = sanitize(request.getParameter("message"));
            String ticketId = LocalDateTime.now().toString();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
                writer.write(ticketId + "|" + username + "|" + subject + "|" + message + "|OPEN|||");
                writer.newLine();
            } catch (IOException e) {
                System.err.println("Failed to save support ticket: " + e.getMessage());
            }

            response.sendRedirect(request.getContextPath() + "/customer-support.jsp?status=sent");
            return;
        }

        if ("resolve_ticket".equals(action)) {
            HttpSession session = request.getSession(false);
            if (!isAdmin(session)) {
                response.sendRedirect(request.getContextPath() + "/admin-login.jsp");
                return;
            }

            String ticketId = normalized(request.getParameter("ticketId"));
            String resolutionNote = sanitize(request.getParameter("resolutionNote"));
            String resolver = session.getAttribute("adminUser") == null
                    ? "staff"
                    : sanitize(String.valueOf(session.getAttribute("adminUser")));

            List<SupportTicket> tickets = readAllTickets();
            for (SupportTicket ticket : tickets) {
                if (ticket.id.equals(ticketId)) {
                    ticket.status = "RESOLVED";
                    ticket.resolvedBy = resolver;
                    ticket.resolvedAt = LocalDateTime.now().toString();
                    ticket.resolutionNote = resolutionNote.isEmpty() ? "Resolved by staff." : resolutionNote;
                    break;
                }
            }
            writeAllTickets(tickets);

            response.sendRedirect(request.getContextPath() + "/support?action=staff_list");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/customer-support.jsp");
    }

    public static class SupportTicket {
        public String id = "";
        public String username = "";
        public String subject = "";
        public String message = "";
        public String status = "OPEN";
        public String resolvedBy = "";
        public String resolvedAt = "";
        public String resolutionNote = "";
    }
}

