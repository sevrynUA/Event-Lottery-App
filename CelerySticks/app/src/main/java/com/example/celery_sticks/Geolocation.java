package com.example.celery_sticks;

public class Geolocation {
    private String address;

    public Geolocation(String location) {
        this.address = location;
    }

    /**
     * Gets province name
     * @return
     * returns province name
     */
    public String getAddress() {
        return address;
    }
}
