package com.example.celery_sticks;

/**
 * This is a class that defines a user profile.
 */
public class Profile {
    private String name;
    private String email;
    private String phoneNumber;

    /**
     * Instantiates an Event object
     * @param name
     * @param email
     * @param phoneNumber
     */
    public Profile(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets User name
     * @return
     * returns user name
     */
    public String getName() {
        return name;
    }

    /**
     * Allows you to change user name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets user email
     * @return
     * returns user email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Allows you to change user email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets user phone number
     * @return
     * returns user phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Allows you to change user phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
