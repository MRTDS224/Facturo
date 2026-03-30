package com.facturo.controller;

import com.facturo.App;
import com.facturo.model.Invoice;
import com.facturo.model.InvoiceItem;
import com.facturo.service.InvoiceService;
import com.facturo.service.PdfGenerationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import com.facturo.model.Client;
import com.facturo.service.ClientService;
import com.facturo.service.SettingsService;
import javafx.scene.control.TextInputDialog;
import org.controlsfx.control.textfield.TextFields;

public class InvoiceController {

    @FXML
    private ComboBox<Client> clientComboBox;
    @FXML
    private CheckBox chkAddress;
    @FXML
    private CheckBox chkPhone;
    @FXML
    private CheckBox chkEmail;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField priceField;
    @FXML
    private TableView<InvoiceItem> itemsTable;
    @FXML
    private TableColumn<InvoiceItem, String> colDescription;
    @FXML
    private TableColumn<InvoiceItem, Integer> colQuantity;
    @FXML
    private TableColumn<InvoiceItem, Double> colPrice;
    @FXML
    private TableColumn<InvoiceItem, Double> colTotal;
    @FXML
    private Label totalLabel;

    private final ObservableList<InvoiceItem> currentItems = FXCollections.observableArrayList();
    private final InvoiceService invoiceService = new InvoiceService();
    private final PdfGenerationService pdfService = new PdfGenerationService();
    private final ClientService clientService = new ClientService();
    private final SettingsService settingsService = new SettingsService();

    @FXML
    public void initialize() {
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        itemsTable.setItems(currentItems);
        updateTotal();

        // Setup Context Menu for Edit/Delete
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Modifier");
        editItem.setOnAction(e -> editItem());
        MenuItem deleteItem = new MenuItem("Supprimer");
        deleteItem.setOnAction(e -> removeItem());
        contextMenu.getItems().addAll(editItem, deleteItem);
        itemsTable.setContextMenu(contextMenu);

        // Setup Client ComboBox
        clientComboBox.setItems(clientService.getAllClients());
        clientComboBox.setConverter(new StringConverter<Client>() {
            @Override
            public String toString(Client client) {
                return client != null ? client.getName() : "";
            }

            @Override
            public Client fromString(String string) {
                return null;
            }
        });

        // Setup Autocomplete for description
        TextFields.bindAutoCompletion(descriptionField, invoiceService.getDistinctDescriptions());
    }

    @FXML
    private void addItem() {
        String desc = descriptionField.getText();

        if (desc == null || desc.trim().isEmpty()) {
            showAlert("Erreur", "La description de l'article est obligatoire.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(quantityField.getText().trim());
            if (qty <= 0) {
                showAlert("Erreur", "La quantité doit être supérieure à zéro.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le format de la quantité est invalide.\nVeuillez entrer un nombre entier positif dans la colonne Quantité.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceField.getText().replace(",", ".").trim());
            if (price < 0) {
                showAlert("Erreur", "Le prix unitaire ne peut pas être négatif.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le format du prix est invalide.\nVeuillez entrer un montant numérique correct dans la colonne Prix Unitaire.");
            return;
        }

        boolean found = false;
        for (InvoiceItem existingItem : currentItems) {
            if (existingItem.getDescription().equalsIgnoreCase(desc) && existingItem.getUnitPrice() == price) {
                existingItem.setQuantity(existingItem.getQuantity() + qty);
                found = true;
                break;
            }
        }

        if (!found) {
            InvoiceItem item = new InvoiceItem(desc, qty, price);
            currentItems.add(item);
        }

        itemsTable.refresh();

        // Clear inputs
        descriptionField.clear();
        quantityField.clear();
        priceField.clear();
        descriptionField.requestFocus();

        updateTotal();
    }

    @FXML
    private void removeItem() {
        InvoiceItem selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentItems.remove(selected);
            updateTotal();
        }
    }

    @FXML
    private void editItem() {
        InvoiceItem selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un article à modifier.");
            return;
        }

        Dialog<InvoiceItem> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'article");
        dialog.setHeaderText("Modifier les détails de l'article");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField descField = new TextField(selected.getDescription());
        TextField qtyField = new TextField(String.valueOf(selected.getQuantity()));
        TextField prcField = new TextField(String.valueOf(selected.getUnitPrice()));

        grid.add(new Label("Description:"), 0, 0);
        grid.add(descField, 1, 0);
        grid.add(new Label("Quantité:"), 0, 1);
        grid.add(qtyField, 1, 1);
        grid.add(new Label("Prix unitaire:"), 0, 2);
        grid.add(prcField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String desc = descField.getText();
                if (desc == null || desc.trim().isEmpty()) {
                    showAlert("Erreur", "La description de l'article est obligatoire.");
                    return null;
                }
                int qty;
                try {
                    qty = Integer.parseInt(qtyField.getText().trim());
                    if (qty <= 0) {
                        showAlert("Erreur", "La quantité doit être supérieure à zéro.");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Le format de la quantité est invalide.\nLa colonne Quantité nécessite un nombre entier.");
                    return null;
                }
                double price;
                try {
                    price = Double.parseDouble(prcField.getText().replace(",", ".").trim());
                    if (price < 0) {
                        showAlert("Erreur", "Le prix unitaire ne peut pas être négatif.");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Le format du prix est invalide.\nLa colonne Prix Unitaire nécessite un montant numérique.");
                    return null;
                }
                return new InvoiceItem(desc, qty, price);
            }
            return null;
        });

        Optional<InvoiceItem> result = dialog.showAndWait();

        result.ifPresent(item -> {
            selected.setDescription(item.getDescription());
            selected.setQuantity(item.getQuantity());
            selected.setUnitPrice(item.getUnitPrice());
            itemsTable.refresh();
            updateTotal();
        });
    }

    private void updateTotal() {
        double total = currentItems.stream().mapToDouble(InvoiceItem::getTotal).sum();
        totalLabel.setText(String.format("%.2f", total));
    }

    @FXML
    private void saveAndExport() {
        Client selectedClient = clientComboBox.getValue();
        if (selectedClient == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Client manquant");
            alert.setHeaderText("Aucun client n'a été sélectionné.");
            alert.setContentText("Voulez-vous choisir un client existant ou ajouter un nouveau client ?");

            ButtonType btnExistant = new ButtonType("Client Existant");
            ButtonType btnNouveau = new ButtonType("Nouveau Client");
            ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(btnExistant, btnNouveau, btnAnnuler);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == btnExistant) {
                    clientComboBox.requestFocus();
                    clientComboBox.show();
                    return;
                } else if (result.get() == btnNouveau) {
                    handleAddClient();
                    selectedClient = clientComboBox.getValue();
                    if (selectedClient == null) {
                        return; // They cancelled the add client dialog
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        
        if (currentItems.isEmpty()) {
            showAlert("Erreur", "Veuillez ajouter des articles à la facture.");
            return;
        }

        String customer = selectedClient.getName();

        // Generate Invoice Number (Simple timestamp based for now)
        String invoiceNum = "INV-" + System.currentTimeMillis();
        Invoice invoice = new Invoice(invoiceNum, selectedClient);
        invoice.setDate(LocalDate.now());
        invoice.getItems().addAll(currentItems);
        invoice.setTotalAmount(Double.parseDouble(totalLabel.getText().replace(",", "."))); // Handle locale if needed

        // Save to DB
        invoiceService.saveInvoice(invoice);

        // Export PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer la facture PDF");
        fileChooser.setInitialFileName("Facture_" + invoiceNum + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(totalLabel.getScene().getWindow());

        if (file != null) {
            String headerText = settingsService.getSetting("invoice_header_text", "FACTURO - FACTURE");
            pdfService.generateInvoicePdf(invoice, file.getAbsolutePath(), chkAddress.isSelected(),
                    chkPhone.isSelected(), chkEmail.isSelected(), headerText);
            showAlert("Succès", "Facture enregistrée et exportée !");
            goBack();
        }
    }

    @FXML
    private void handleSettings() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Paramètres de l'entreprise");
        dialog.setHeaderText("Modifier les informations de l'entreprise");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField companyName = new TextField(settingsService.getSetting("company_name", "Facturo Entreprise"));
        companyName.setPromptText("Nom de l'entreprise");
        TextField companyAddress = new TextField(settingsService.getSetting("company_address", ""));
        companyAddress.setPromptText("Adresse");
        TextField companyPhone = new TextField(settingsService.getSetting("company_phone", ""));
        companyPhone.setPromptText("Téléphone");
        TextField companyEmail = new TextField(settingsService.getSetting("company_email", ""));
        companyEmail.setPromptText("Email");

        // Logo selection
        TextField companyLogo = new TextField(settingsService.getSetting("company_logo", ""));
        companyLogo.setPromptText("Chemin du Logo");
        companyLogo.setEditable(false);
        Button browseLogoBtn = new Button("Parcourir...");
        browseLogoBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sélectionner un logo");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File selectedFile = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (selectedFile != null) {
                companyLogo.setText(selectedFile.getAbsolutePath());
            }
        });

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(companyName, 1, 0);
        grid.add(new Label("Adresse:"), 0, 1);
        grid.add(companyAddress, 1, 1);
        grid.add(new Label("Téléphone:"), 0, 2);
        grid.add(companyPhone, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(companyEmail, 1, 3);
        grid.add(new Label("Logo:"), 0, 4);
        grid.add(companyLogo, 1, 4);
        grid.add(browseLogoBtn, 2, 4);

        // Logs Export
        Button openLogsBtn = new Button("Ouvrir dossier Logs");
        openLogsBtn.setOnAction(e -> {
            try {
                String cmd = "explorer.exe"; // Windows Specific
                new ProcessBuilder(cmd, com.facturo.service.LogService.getAppDir()).start();
            } catch (Exception ex) {
                com.facturo.service.LogService.error("Failed to open log folder", ex);
                showAlert("Erreur", "Impossible d'ouvrir le dossier des journaux.");
            }
        });
        grid.add(openLogsBtn, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                settingsService.saveSetting("company_name", companyName.getText());
                settingsService.saveSetting("company_address", companyAddress.getText());
                settingsService.saveSetting("company_phone", companyPhone.getText());
                settingsService.saveSetting("company_email", companyEmail.getText());
                settingsService.saveSetting("company_logo", companyLogo.getText());

                // Keep the old header setting just in case it's used elsewhere, or just
                // override it.
                settingsService.saveSetting("invoice_header_text", companyName.getText());

                showAlert("Succès", "Paramètres sauvegardés avec succès.");
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleAddClient() {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Nouveau Client");
        dialog.setHeaderText("Ajouter un nouveau client");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Nom");
        TextField address = new TextField();
        address.setPromptText("Adresse");
        TextField phone = new TextField();
        phone.setPromptText("Téléphone");
        TextField email = new TextField();
        email.setPromptText("Email");

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
            String addResult = clientService.addClient(client);
            if ("SUCCESS".equals(addResult)) {
                clientComboBox.setItems(clientService.getAllClients());
                // Sélect the newly added client
                for (Client c : clientComboBox.getItems()) {
                    if (c.getName().equals(client.getName())) {
                        clientComboBox.getSelectionModel().select(c);
                        break;
                    }
                }
            } else {
                showAlert("Erreur", addResult);
            }
        });
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
