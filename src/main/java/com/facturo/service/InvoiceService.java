package com.facturo.service;

import com.facturo.model.Invoice;
import com.facturo.model.InvoiceItem;
import java.sql.*;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InvoiceService {

    public void saveInvoice(Invoice invoice) {
        String sqlInvoice = "INSERT INTO invoices(invoice_number, date, customer_name, total_amount) VALUES(?, ?, ?, ?)";
        String sqlItem = "INSERT INTO invoice_items(invoice_number, description, quantity, unit_price, total) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseService.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sqlInvoice)) {
                pstmt.setString(1, invoice.getInvoiceNumber());
                pstmt.setString(2, invoice.getDate().toString());
                pstmt.setString(3, invoice.getCustomerName());
                pstmt.setDouble(4, invoice.getTotalAmount());
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmtItem = conn.prepareStatement(sqlItem)) {
                for (InvoiceItem item : invoice.getItems()) {
                    pstmtItem.setString(1, invoice.getInvoiceNumber());
                    pstmtItem.setString(2, item.getDescription());
                    pstmtItem.setInt(3, item.getQuantity());
                    pstmtItem.setDouble(4, item.getUnitPrice());
                    pstmtItem.setDouble(5, item.getTotal());
                    pstmtItem.addBatch();
                }
                pstmtItem.executeBatch();
            }

            conn.commit();
            System.out.println("Invoice saved successfully.");

        } catch (SQLException e) {
            System.err.println("Error saving invoice: " + e.getMessage());
        }
    }

    public ObservableList<Invoice> getAllInvoices() {
        ObservableList<Invoice> invoices = FXCollections.observableArrayList();
        String sql = "SELECT * FROM invoices";

        try (Connection conn = DatabaseService.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String number = rs.getString("invoice_number");
                String customer = rs.getString("customer_name");
                Invoice invoice = new Invoice(number, customer);
                invoice.setDate(LocalDate.parse(rs.getString("date")));
                invoice.setTotalAmount(rs.getDouble("total_amount"));
                // Items loading could be lazy or here. Keeping simple for now.
                invoices.add(invoice);
            }

        } catch (SQLException e) {
            System.err.println("Error loading invoices: " + e.getMessage());
        }
        return invoices;
    }

    public void loadItemsForInvoice(Invoice invoice) {
        String sql = "SELECT * FROM invoice_items WHERE invoice_number = ?";

        try (Connection conn = DatabaseService.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, invoice.getInvoiceNumber());
            try (ResultSet rs = pstmt.executeQuery()) {
                invoice.getItems().clear();
                while (rs.next()) {
                    String desc = rs.getString("description");
                    int qty = rs.getInt("quantity");
                    double price = rs.getDouble("unit_price");
                    InvoiceItem item = new InvoiceItem(desc, qty, price);
                    invoice.addItem(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading invoice items: " + e.getMessage());
        }
    }
}
