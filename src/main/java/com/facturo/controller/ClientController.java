package com.facturo.controller;

import com.facturo.App;
import com.facturo.model.Client;
import com.facturo.service.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;

public class ClientController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private TableView<Client> clientTable;
    @FXML
    private TableColumn<Client, String> colName;
    @FXML
    private TableColumn<Client, String> colAddress;
    @FXML
    private TableColumn<Client, String> colPhone;
    @FXML
    private TableColumn<Client, String> colEmail;

    private final ClientService clientService = new ClientService();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        loadClients();
    }

    private void loadClients() {
        clientTable.setItems(clientService.getAllClients());
    }

    @FXML
    private void addClient() {
        String name = nameField.getText();
        String address = addressField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();

        if (name.isEmpty()) {
            showAlert("Erreur", "Le nom du client est obligatoire.");
            return;
        }

        Client client = new Client(name, address, phone, email);
        if (clientService.addClient(client)) {
            loadClients();
            nameField.clear();
            addressField.clear();
            phoneField.clear();
            emailField.clear();
            showAlert("Succès", "Client ajouté !");
        } else {
            showAlert("Erreur", "Impossible d'ajouter le client (Nom déjà existant ?).");
        }
    }

    @FXML
    private void goBack() {
        try {
            App.setRoot("view/dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
