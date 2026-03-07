package com.facturo.service;

import com.facturo.model.Client;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClientService {

    public String addClient(Client client) {
        String sql = "INSERT INTO clients(name, address, phone, email) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseService.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, client.getName());
            pstmt.setString(2, client.getAddress());
            pstmt.setString(3, client.getPhone());
            pstmt.setString(4, client.getEmail());
            pstmt.executeUpdate();
            LogService.info("Client added successfully: " + client.getName());
            return "SUCCESS";
        } catch (SQLException e) {
            LogService.error("Error adding client: " + client.getName(), e);
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed: clients.name")) {
                return "Ce nom de client existe déjà dans la base de données.";
            }
            return "Erreur d'enregistrement: " + e.getMessage();
        }
    }

    public ObservableList<Client> getAllClients() {
        ObservableList<Client> clients = FXCollections.observableArrayList();
        String sql = "SELECT * FROM clients";

        try (Connection conn = DatabaseService.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clients.add(new Client(
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email")));
            }
        } catch (SQLException e) {
            System.err.println("Error loading clients: " + e.getMessage());
        }
        return clients;
    }
}
