package com.facturo.controller;

import com.facturo.App;
import java.io.IOException;
import javafx.fxml.FXML;

public class DashboardController {

    @FXML
    private void createInvoice() throws IOException {
        App.setRoot("view/invoice_form");
    }

    @FXML
    private void viewHistory() throws IOException {
        App.setRoot("view/history");
    }

    @FXML
    private void manageClients() throws IOException {
        App.setRoot("view/client");
    }
}
