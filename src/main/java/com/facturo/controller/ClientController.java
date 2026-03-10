package com.facturo.controller;

import com.facturo.App;
import com.facturo.model.Client;
import com.facturo.service.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.util.Optional;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

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

        // Setup Context Menu for Edit/Delete
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Modifier");
        editItem.setOnAction(e -> editClient());
        MenuItem deleteItem = new MenuItem("Supprimer");
        deleteItem.setOnAction(e -> deleteSelectedClient());
        contextMenu.getItems().addAll(editItem, deleteItem);
        clientTable.setContextMenu(contextMenu);

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
        String result = clientService.addClient(client);
        if ("SUCCESS".equals(result)) {
            loadClients();
            nameField.clear();
            addressField.clear();
            phoneField.clear();
            emailField.clear();
            showAlert("Succès", "Client ajouté !");
        } else {
            showAlert("Erreur", result);
        }
    }

    @FXML
    private void editClient() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un client à modifier.");
            return;
        }

        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Modifier Client");
        dialog.setHeaderText("Modifier les informations du client");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField(selected.getName());
        TextField address = new TextField(selected.getAddress());
        TextField phone = new TextField(selected.getPhone());
        TextField email = new TextField(selected.getEmail());

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Adresse:"), 0, 1);
        grid.add(address, 1, 1);
        grid.add(new Label("Téléphone:"), 0, 2);
        grid.add(phone, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(email, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (name.getText().isEmpty())
                    return null;
                return new Client(name.getText(), address.getText(), phone.getText(), email.getText());
            }
            return null;
        });

        Optional<Client> result = dialog.showAndWait();

        result.ifPresent(client -> {
            String updateRes = clientService.updateClient(selected.getName(), client);
            if ("SUCCESS".equals(updateRes)) {
                loadClients();
                showAlert("Succès", "Client modifié avec succès.");
            } else {
                showAlert("Erreur", updateRes);
            }
        });
    }

    @FXML
    private void deleteSelectedClient() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Voulez-vous vraiment supprimer le client " + selected.getName() + " ?", ButtonType.YES,
                    ButtonType.NO);
            confirm.setHeaderText("Confirmation de suppression");
            confirm.showAndWait();
            if (confirm.getResult() == ButtonType.YES) {
                String res = clientService.deleteClient(selected.getName());
                if ("SUCCESS".equals(res)) {
                    loadClients();
                    showAlert("Succès", "Client supprimé avec succès.");
                } else {
                    showAlert("Erreur", res);
                }
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner un client à supprimer.");
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
