package com.example.celery_sticks;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is a class that defines an event.
 */
public class Event {
    private String eventID;

    private Waitlist tentativeEntrants;
    private ArrayList<Entrant> finalEntrants;

    private String eventName;
    private String description;
    private Date eventDate;
    private String location;
    private int maxCapacity; // Optional max capacity
    private String posterImage; // Path to the event poster image

    private QRCode detailsQRCode;
    private QRCode signupQRCcode;

    /**
     * Instantiates an Event object
     * @param eventName
     * @param eventID
     * @param eventDescription
     * @param eventImage
     * @param eventDate
     * @param eventClose
     * @param eventOpen
     * @param eventDetailsQR
     * @param eventSignUpQR
     * @param eventLocation
     */
    public Event(String eventName, String eventID, String eventDescription, String eventImage, Timestamp eventDate, Timestamp eventClose, Timestamp eventOpen, String eventDetailsQR, String eventSignUpQR, String eventLocation) {
        this.eventName = eventName;
        this.eventID = eventID;
        this.description = eventDescription;
        this.posterImage = eventImage;
        this.eventDate = eventDate.toDate();
        this.location = eventLocation;
        this.tentativeEntrants = new Waitlist();
    }

    /**
     * Gets event date
     * @return
     * returns event date
     */
    public Date getEventDate() {
        return eventDate;
    }

    /**
     * Allows you to set or change event date
     */
    public void setEventDate(Date date) {
        this.eventDate = date;
    }

    /**
     * Gets event name
     * @return
     * returns event name
     */
    public String getEventName() {
        return eventName;
    }


    /**
     * Allows you to set or change event name
     */
    public void setEventName(String string) {
        this.eventName = string;
    }

    /**
     * Gets event description
     * @return
     * returns event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Allows you to set or change event description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets event location
     * @return
     * returns event location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Allows you to set or change event location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets optional max capacity for event
     * @return
     * returns optional max capacity for event
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Allows you to set or change optional event max capacity
     */
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    /**
     * Gets events poster image
     * @return
     * returns Event poster image
     */
    public String getPosterImage() {
        return posterImage;
    }

    /**
     * Allows you to set or change event poster image
     */
    public void setPosterImage(String posterImage) {
        this.posterImage = posterImage;
    }

    /**
     * Gets events ID
     * @return
     * returns Event poster image
     */
    public String getEventId() {
        return eventID;
    }

    /**
     * Sets event ID
     */
    public void setEventId(String string) {
        this.eventID = string;
    }


    /**
     * Gets QR code details for event
     * @return
     * returns event QR code details
     */
    public QRCode getDetailsQRCode() {
        return detailsQRCode;
    }


    /**
     * Allows you to set QR details for event
     */
    public void setDetailsQRCode(QRCode qrcode) {
        this.detailsQRCode = qrcode;
    }

    /**
     * Gets QR code for event
     * @return
     * returns event QR code
     */
    public QRCode getSignupQRCode() {
        return signupQRCcode;
    }

    /**
     * Allows you to set or change QR code for event
     */
    public void setSignupQRCode(QRCode qrcode) {
        this.signupQRCcode = qrcode;
    }

    /**
     * Gets event Waitlist
     * @return
     * returns waitlist
     */
    public List<Entrant> getWaitlistEntrants() {
        return tentativeEntrants.getEntrants();
    }

    /**
     * Allows users to be added to waitlist
     */
    public void addToWaitlist(Entrant entrant) {
        tentativeEntrants.addEntrant(entrant);
    }

    /**
     * Allows users to be removed from waitlist
     */
    public void removeFromWaitlist(Entrant entrant) {
        tentativeEntrants.removeEntrant(entrant);
    }

}
