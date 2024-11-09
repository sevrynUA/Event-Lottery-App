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
    private String eventName;
    private String description;
    private Date eventDate;
    private String location;
    private int maxCapacity; // Optional max capacity
    private String posterImage; // Path to the event poster image
    private Boolean lotteryStatus;
    private String encodedImage;

    /**
     * Instantiates an Event object
     * @param eventName
     * @param eventID
     * @param eventDescription
     * @param eventImage
     * @param eventDate
     * @param eventClose
     * @param eventOpen
     * @param eventQR
     * @param eventLocation
     */
    public Event(String eventName, String eventID, String eventDescription, String eventImage, Timestamp eventDate, Timestamp eventClose, Timestamp eventOpen, String eventQR, String eventLocation) {
        this.eventName = eventName;
        this.eventID = eventID;
        this.description = eventDescription;
        this.posterImage = eventImage;
        this.eventDate = eventDate.toDate();
        this.location = eventLocation;
        lotteryStatus = false;
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
     * Gets whether or not the lottery process has started
     * @return
     * Whether the lottery process has started or not
     */
    public Boolean getLotteryStatus() {
        return lotteryStatus;
    }

    /**
     * This updates the status of the lottery
     * @param lotteryStatus
     * The status of the lottery
     */
    public void setLotteryStatus(Boolean lotteryStatus) {
        this.lotteryStatus = lotteryStatus;
    }

    /**
     * returns the encoded image string
     * @return
     * the encoded image string
     */
    public String getEncodedImage() {
        return encodedImage;
    }

    /**
     * updates the encoded image
     * @param encodedImage string to add
     *
     */
    public void setLotteryStatus(String encodedImage) {
        this.encodedImage = encodedImage;
    }

}
