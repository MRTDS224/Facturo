package com.facturo.controller;

import com.facturo.App;
import com.facturo.model.Invoice;
import com.facturo.service.InvoiceService;
import com.facturo.service.PdfGenerationService;
import com.facturo.service.SettingsService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class HistoryController {

    @FXML
    private TableView<Invoice> invoiceTable;
    @FXML
    private TableColumn<Invoice, String> colInvoiceNumber;
    @FXML
    private TableColumn<Invoice, LocalDate> colDate;
    @FXML
    private TableColumn<Invoice, String> colCustomer;
    @FXML
    private TableColumn<Invoice, Double> colTotal;

    private final InvoiceService invoiceService = new InvoiceService();
    private final PdfGenerationService pdfService = new PdfGenerationService();
    private final SettingsService settingsService = new SettingsService();

    @FXML
    public void initialize() {
        colInvoiceNumber.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        loadInvoices();
    }

    private void loadInvoices() {
        invoiceTable.setItems(invoiceService.getAllInvoices());
    }

    @FXML
    private void exportSelected() {
        Invoice selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une facture.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter PDF");
        fileChooser.setInitialFileName("Facture_" + selected.getInvoiceNumber() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(invoiceTable.getScene().getWindow());

        if (file != null) {
            // Re-load items for the selected invoice if not fully loaded (omitted for now
            // as simple object)
            // TODO: Ensure items are loaded if needed for PDF re-generation

            // For MVP, we need items to generate PDF.
            // Current getAllInvoices() in InvoiceService doesn't load items.
            // We should add a method to load items or load them eagerly.
            // Let's assume for now we might need to fetch them.

            // Quick fix: Fetch items for the invoice before generating PDF
            invoiceService.loadItemsForInvoice(selected);

            String headerText = settingsService.getSetting("invoice_header_text", "FACTURO - FACTURE");

            pdfService.generateInvoicePdf(selected, file.getAbsolutePath(), true, true, true, headerText);
            showAlert("Succès", "Facture exportée !");
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
