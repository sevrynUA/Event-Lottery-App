package com.example.celery_sticks;

import java.util.ArrayList;

/**
 * This is a class that defines an entrant
 */
public class Entrant {
    // The Entrant's device
    private String user;

    // The events that the Entrant has joined the waitlist of
    private ArrayList<Event> wishlist = new ArrayList<>();

    // The events that the Entrant is going to attend
    private ArrayList<Event> attending = new ArrayList<>();

    // The Entrant's profile
    private Profile profile = new Profile();

    // Whether the Entrant wants to have notifications on or not (on by default)
    private boolean notifications = true;

    /**
     * This creates a new entrant
     * @param user
     * This is the user's device
     */
    public Entrant(String user) {
        this.user = user;
    }

    /**
     * This returns the Entrant's device
     * @return
     * The Entrant's device
     */
    public String getUser() {
        return user;
    }

    /**
     * This adds an event to the user's wishlist
     * @param event
     * The event that the user joined the waitlist for
     */
    public void joinWaitlist(Event event) {
        wishlist.add(event);
    }

    /**
     * This removes an event to the user's wishlist
     * @param event
     * The event that the user no longer wants to be on the waitlist for
     */
    public void leaveWaitlist(Event event){
        if (wishlist.contains(event)) {
            wishlist.remove(event);
        }else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * This returns the user's wishlist
     * @return
     * A list of event's the user has waitlisted
     */
    public ArrayList<Event> getWishlist() {
        return wishlist;
    }

    /**
     * This adds an event to the user's list of events that they are going to attend
     * @param event
     * This is the event the user is going to attend
     */
    public void joinEvent(Event event){
        attending.add(event);
    }

    /**
     * This removes an event from the user's list of events that they are going to attend
     * @param event
     * The event that the user no longer is going to attend
     */
    public void leaveEvent(Event event){
        if (attending.contains(event)) {
            attending.remove(event);
        }else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * This returns the list of events the user is going to attend
     * @return
     * The list of events the user is going to attend
     */
    public ArrayList<Event> getAttending() {
        return attending;
    }

    /**
     * This updates the Entrant's name in their profile
     * @param name
     * The Entrant's name
     */
    public void updateName(String name){
        profile.setName(name);
    }

    /**
     * This updates the Entrant's email in their profile
     * @param email
     * The Entrant's email
     */
    public void updateEmail(String email){
        profile.setEmail(email);
    }

    /**
     * This updates the Entrant's contact in their profile
     * @param contact
     * The Entrant's contact
     */
    public void updateContact(String contact){
        profile.setContact(contact);
    }

    /**
     * This returns the user's profile
     * @return
     * The user's profile
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * This turns notifications off
     */
    public void turnOnNotifications(){
        notifications = true;
    }

    /**
     * This turns notifications on
     */
    public void turnOffNotifications(){
        notifications = false;
    }

    /**
     * This returns whether notification are on or off for this user
     * @return
     * whether the notifications are on or off
     */
    public boolean isNotifications() {
        return notifications;
    }

