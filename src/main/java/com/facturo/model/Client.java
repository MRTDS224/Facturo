package com.facturo.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Client {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();

    public Client(String name, String address, String phone, String email) {
        setName(name);
        setAddress(address);
        setPhone(phone);
        setEmail(email);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String value) {
        address.set(value);
    }

    public StringProperty addressProperty() {
        return address;
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String value) {
        phone.set(value);
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String value) {
        email.set(value);
    }

    public StringProperty emailProperty() {
        return email;
    }

    @Override
    public String toString() {
        return getName();
    }
}
