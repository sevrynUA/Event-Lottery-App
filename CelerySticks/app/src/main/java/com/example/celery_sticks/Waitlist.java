package com.example.celery_sticks;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that defines a waitlist for an event.
 */
public class Waitlist {
    private List<Entrant> entrants;

    /**
     * Instantiates a waitlist
     */
    public Waitlist() {
        this.entrants = new ArrayList<>();
    }

    /**
     * Adds entrant to waitlist if they are not already on it
     */
    public void addEntrant(Entrant entrant) {
        if (!entrants.contains(entrant)) {
            entrants.add(entrant);
        }
    }

    /**
     * Removes entrant from waitlist
     */
    public void removeEntrant(Entrant entrant) {
        entrants.remove(entrant);
    }

    /**
     * Gets a list of all entrants on waitlist
     * @return
     * List of entrants
     */
    public List<Entrant> getEntrants() {
        return entrants;
    }
}
