package com.example.celery_sticks;

import java.util.ArrayList;

/**
 * Represents a user storing basic personal information, including first name, last name, email,
 * and optionally a phone number.
 */
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;
    private String userID;
    private String encodedImage;

    /**
     * Constructs a User object with the specified first name, last name, email, role, and ID.
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param email     the email address of the user
     * @param role      the role of the user
     * @param userID    the ID of the user
     */
    public User(String firstName, String lastName, String email, String role, String userID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.userID = userID;
    }

    /**
     * Constructs a User object with the specified first name, last name, email, phone number, role, and ID.
     * @param firstName     the first name of the user
     * @param lastName      the last name of the user
     * @param email         the email address of the user
     * @param phoneNumber   the phone number of the user
     * @param role          the role of the user
     * @param userID        the ID of the user
     */
    public User(String firstName, String lastName, String email, String phoneNumber, String role, String userID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.userID = userID;
    }

    /**
     * Gets the ID of the user
     * @return the ID of the user
     */
    public String getUserID() { return userID; }

    /**
     * Sets the ID of the user
     * @param userID the ID to set
     */
    public void setUserID(String userID) { this.userID = userID; }

    /**
     * Gets the roles of the user
     * @return
     */
    public String getRole() { return role; }

    /**
     * Sets the role of the user
     * @param role the role to set
     */
    public void setRole(String role) { this.role = role; }

    /**
     * Gets the first name of the user.
     * @return  the first name of the user
     */
    public String getFirstName() { return firstName; }

    /**
     * Sets the first name of the user.
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /**
     * Gets the last name of the user
     * @return  the last name of the user
     */
    public String getLastName() { return lastName; }

    /**
     * Sets the last name of the user.
     * @param lastName  the last name to set
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * Gets the email address of the user.
     * @return  the email address of the user
     */
    public String getEmail() { return email; }

    /**
     * Sets the email address of the user.
     * @param email the email address to set
     */
    public void setEmail(String email) { this.email = email;}

    /**
     * Gets the phone number of the user.
     * @return  the phone number of the user
     */
    public String getPhoneNumber() { return phoneNumber; }

    /**
     * Sets the phone number of the user.
     * @param phoneNumber   the phone number to set
     */
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }


    /**
     * Gets the profile image of the user.
     * @return  the encoded string
     */
    public String getEncodedImage() { return encodedImage; }

    /**
     * Sets the profile image of the user.
     * @param data   the image string
     */
    public void setEncodedImage(String data) { this.encodedImage = data; }

}