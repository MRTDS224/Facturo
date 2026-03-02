package com.facturo.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;

public class Invoice {
    private final StringProperty invoiceNumber = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>(LocalDate.now());
    private final StringProperty customerName = new SimpleStringProperty();
    private Client client; // To hold all client details for the PDF export
    private final ObservableList<InvoiceItem> items = FXCollections.observableArrayList();
    private final DoubleProperty totalAmount = new SimpleDoubleProperty(0.0);

    public Invoice(String number, String customer) {
        setInvoiceNumber(number);
        setCustomerName(customer);
    }

    public Invoice(String number, Client client) {
        setInvoiceNumber(number);
        this.client = client;
        if (client != null) {
            setCustomerName(client.getName());
        }
    }

    public void addItem(InvoiceItem item) {
        items.add(item);
        recalculateTotal();
    }

    public void removeItem(InvoiceItem item) {
        items.remove(item);
        recalculateTotal();
    }

    private void recalculateTotal() {
        double sum = items.stream().mapToDouble(InvoiceItem::getTotal).sum();
        setTotalAmount(sum);
    }

    // Getters and Setters
    public String getInvoiceNumber() {
        return invoiceNumber.get();
    }

    public void setInvoiceNumber(String value) {
        invoiceNumber.set(value);
    }

    public StringProperty invoiceNumberProperty() {
        return invoiceNumber;
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate value) {
        date.set(value);
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public void setCustomerName(String value) {
        customerName.set(value);
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ObservableList<InvoiceItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount.get();
    }

    public void setTotalAmount(double value) {
        totalAmount.set(value);
    }

    public DoubleProperty totalAmountProperty() {
        return totalAmount;
    }
}
