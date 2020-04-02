package com.example.autopark.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Map<String, Object> address;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, Object> getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public User setAddress(String country, String city, String street, int houseNumber) {
        this.address = new HashMap<>();
        address.put("country", country);
        address.put("city", city);
        address.put("street", street);
        address.put("houseNumber", houseNumber);
        return this;
    }
}
