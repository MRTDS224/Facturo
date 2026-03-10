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

    public String updateClient(String oldName, Client updatedClient) {
        String sql = "UPDATE clients SET name = ?, address = ?, phone = ?, email = ? WHERE name = ?";
        try (Connection conn = DatabaseService.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, updatedClient.getName());
            pstmt.setString(2, updatedClient.getAddress());
            pstmt.setString(3, updatedClient.getPhone());
            pstmt.setString(4, updatedClient.getEmail());
            pstmt.setString(5, oldName);
            pstmt.executeUpdate();
            LogService.info("Client updated successfully: " + updatedClient.getName());
            return "SUCCESS";
        } catch (SQLException e) {
            LogService.error("Error updating client: " + updatedClient.getName(), e);
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed: clients.name")) {
                return "Ce nom de client existe déjà dans la base de données.";
            }
            return "Erreur de mise à jour: " + e.getMessage();
        }
    }

    public String deleteClient(String name) {
        String sql = "DELETE FROM clients WHERE name = ?";
        try (Connection conn = DatabaseService.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                LogService.info("Client deleted successfully: " + name);
                return "SUCCESS";
            } else {
                return "Client non trouvé.";
            }
        } catch (SQLException e) {
            LogService.error("Error deleting client: " + name, e);
            if (e.getMessage() != null && e.getMessage().contains("FOREIGN KEY constraint failed")) {
                return "Impossible de supprimer ce client car des factures lui sont associées.";
            }
            return "Erreur de suppression: " + e.getMessage();
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
