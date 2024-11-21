package com.example.celery_sticks;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests User class
 */
public class TestUser {
    /**
     * Creates new User
     * @return
     * return newUser of type User
     */
    private User newUser() {
        User newUser = new User("TestUser", "TestLast", "Test@Test.com", "organizer", "1");
        return newUser;
    }

    /**
     * Tests User class getters
     */
    @Test
    public void testNewUser() {
        User user = newUser();

        assertEquals("TestUser", user.getFirstName());
        assertEquals("TestLast", user.getLastName());
        assertEquals("Test@Test.com", user.getEmail());
        assertEquals("organizer", user.getRole());
        assertEquals("1", user.getUserID());

    }

}