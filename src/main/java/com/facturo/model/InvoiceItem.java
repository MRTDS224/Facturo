package com.facturo.model;

import javafx.beans.property.*;

public class InvoiceItem {
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final DoubleProperty unitPrice = new SimpleDoubleProperty();
    private final DoubleProperty total = new SimpleDoubleProperty();

    public InvoiceItem(String description, int quantity, double unitPrice) {
        setDescription(description);
        setQuantity(quantity);
        setUnitPrice(unitPrice);
        updateTotal();
    }

    private void updateTotal() {
        setTotal(getQuantity() * getUnitPrice());
    }

    // Getters and Setters
    public String getDescription() {
        return description.get();
    }

    public void setDescription(String value) {
        description.set(value);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int value) {
        quantity.set(value);
        updateTotal();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice.get();
    }

    public void setUnitPrice(double value) {
        unitPrice.set(value);
        updateTotal();
    }

    public DoubleProperty unitPriceProperty() {
        return unitPrice;
    }

    public double getTotal() {
        return total.get();
    }

    public void setTotal(double value) {
        total.set(value);
    }

    public DoubleProperty totalProperty() {
        return total;
    }
}
