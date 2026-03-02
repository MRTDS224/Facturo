package com.facturo.service;

import com.facturo.model.Client;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClientService {

    public boolean addClient(Client client) {
        String sql = "INSERT INTO clients(name, address, phone, email) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseService.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, client.getName());
            pstmt.setString(2, client.getAddress());
            pstmt.setString(3, client.getPhone());
            pstmt.setString(4, client.getEmail());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding client: " + e.getMessage());
            return false;
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
