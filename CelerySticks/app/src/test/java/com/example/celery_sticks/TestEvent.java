package com.example.celery_sticks;

import static org.junit.Assert.assertEquals;

import com.google.firebase.Timestamp;

import org.junit.Test;

import java.util.Date;

/**
 * Tests Event class
 */
public class TestEvent {
    /**
     * Creates new Event
     * @return
     * return newEvent of type Event
     */
    private Event newEvent() {
        Event newEvent = new Event("TestEvent", "1", "A New Event", "0", new Timestamp(new Date(2026, 10, 10)), new Timestamp(new Date(2026, 10, 10)), new Timestamp(new Date(2026, 10, 10)), "2", "Germany");
        return newEvent;
    }

    /**
     * Tests Event class getters
     */
    @Test
    public void testEvent() {
        Event event = newEvent();

        assertEquals("TestEvent", event.getEventName());
        assertEquals("1", event.getEventId());
        assertEquals("A New Event", event.getDescription());
        assertEquals("0", event.getPosterImage());
        assertEquals("Germany", event.getLocation());

    }

}

