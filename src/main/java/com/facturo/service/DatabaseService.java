package com.facturo.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseService {
    private static String getDbUrl() {
        return "jdbc:sqlite:" + LogService.getAppDir() + java.io.File.separator + "facturo.db";
    }

    public static void initialize() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            // Invoices table
            String sqlInvoices = "CREATE TABLE IF NOT EXISTS invoices (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "invoice_number TEXT NOT NULL UNIQUE," +
                    "date TEXT NOT NULL," +
                    "customer_name TEXT NOT NULL," +
                    "total_amount REAL NOT NULL" +
                    ");";
            stmt.execute(sqlInvoices);

            // Invoice Items table
            String sqlItems = "CREATE TABLE IF NOT EXISTS invoice_items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "invoice_number TEXT NOT NULL," +
                    "description TEXT NOT NULL," +
                    "quantity INTEGER NOT NULL," +
                    "unit_price REAL NOT NULL," +
                    "total REAL NOT NULL," +
                    "FOREIGN KEY (invoice_number) REFERENCES invoices(invoice_number)" +
                    ");";
            stmt.execute(sqlItems);

            // Clients table
            String sqlClients = "CREATE TABLE IF NOT EXISTS clients (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL UNIQUE," +
                    "address TEXT," +
                    "phone TEXT," +
                    "email TEXT" +
                    ");";
            stmt.execute(sqlClients);

            // Settings table
            String sqlSettings = "CREATE TABLE IF NOT EXISTS settings (" +
                    "key TEXT PRIMARY KEY," +
                    "value TEXT NOT NULL" +
                    ");";
            stmt.execute(sqlSettings);

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getDbUrl());
    }
}
