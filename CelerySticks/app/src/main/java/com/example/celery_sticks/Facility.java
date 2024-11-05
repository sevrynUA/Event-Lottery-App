package com.example.celery_sticks;

/**
 * Represents a facility storing basic information, including facility name, email, phone number,
 * and owner ID.
 */
public class Facility {
    private String facilityName;
    private String email;
    private String phoneNumber;
    private String ownerID;

    /**
     * Constructs a Facility object with the specified facility name, email, phone number, and owner ID.
     * @param facilityName  the name of the facility
     * @param email         the email address of the facility
     * @param phoneNumber   the phone number of the facility
     * @param ownerID       the ID of the owner of the facility
     */
    public Facility(String facilityName, String email, String phoneNumber, String ownerID) {
        this.facilityName = facilityName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.ownerID = ownerID;
    }

    /**
     * Constructs a Facility object with the specified facility name, email, and owner ID.
     * @param facilityName  the name of the facility
     * @param email         the email address of the facility
     * @param ownerID       the ID of the owner of the facility
     */
    public Facility(String facilityName, String email, String ownerID) {
        this.facilityName = facilityName;
        this.email = email;
        this.ownerID = ownerID;
    }

    /**
     * Gets the name of the facility
     * @return the name of the facility
     */
    public String getFacilityName() {
        return facilityName;
    }

    /**
     * Gets the email address of the facility
     * @return the email address of the facility
     */
    public String getFacilityEmail() {
        return email;
    }

    /**
     * Gets the phone number of the facility
     * @return the phone number of the facility
     */
    public String getFacilityPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets the ID of the owner of the facility
     * @return the ID of the owner of the facility
     */
    public String getOwnerID() {
        return ownerID;
    }

    /**
     * Sets the name of the facility
     * @param facilityName the name to set
     */
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    /**
     * Sets the email address of the facility
     * @param email the email to set
     */
    public void setFacilityEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the phone number of the facility
     * @param phoneNumber the phone number to set
     */
    public void setFacilityPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Sets the ID of the owner of the facility
     * @param ownerID the ID to set
     */
    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }
}

