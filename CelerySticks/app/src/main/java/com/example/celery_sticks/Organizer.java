package com.example.celery_sticks;

import java.util.ArrayList;

/**
 * This is a class that defines an Organizer
 */
public class Organizer {
    // The Organizer's device
    private String user;

    // The events that the Organizer has created
    private ArrayList<Event> postings = new ArrayList<>();

    // This is the facility profile
    private Profile facilityProfile;

    /**
     * This creates a new Organizer
     *
     * @param user This is the user's device
     */
    public Organizer(String user) {
        this.user = user;
    }

    /**
     * This returns the Organizer's device
     *
     * @return The Organizer's device
     */
    public String getUser() {
        return user;
    }

    /**
     * This adds an Event to the organizers postings
     *
     * @param event The event to be added
     */
    public void addEvent(Event event) {
        postings.add(event);
    }

    /**
     * This removes an Event to the organizers postings
     *
     * @param event The event to be removed
     */
    public void deleteEvent(Event event) {
        if (postings.contains(event)) {
            postings.remove(event);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * This returns the postings of the Organizer
     *
     * @return The list of events the organizer has created
     */
    public ArrayList<Event> getPostings() {
        return postings;
    }

    /**
     * This updates the facility's name in the profile
     *
     * @param name The facility's name
     */
    public void updateName(String name) {
        facilityProfile.setName(name);
    }

    /**
     * This updates the facility's email in the profile
     *
     * @param email The facility's email
     */
    public void updateEmail(String email) {
        facilityProfile.setEmail(email);
    }

    /**
     * This returns the facility's profile
     *
     * @return The facility's profile
     */
    public Profile getFacilityProfile() {
        return facilityProfile;
    }
}

