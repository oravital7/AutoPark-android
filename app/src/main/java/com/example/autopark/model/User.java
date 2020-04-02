package com.example.autopark.model;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String country;
    private String city;
    private String street;
    private String houseNumber;
    private String phoneNumber;
    private String password;

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

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
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

    public User setCountry(String country) {
        this.country = country;
        return this;
    }

    public User setCity(String city) {
        this.city = city;
        return this;
    }

    public User setStreet(String street) {
        this.street = street;
        return this;
    }

    public User setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public User setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }
}
