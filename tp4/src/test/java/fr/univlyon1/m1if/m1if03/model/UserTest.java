package fr.univlyon1.m1if.m1if03.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {
    private final User user = new User("toto", "password", "Xyz");

    @Test
    void getLogin() {
        assertEquals(user.getLogin(), "toto");
    }

    @Test
    void verifyPassword() {
        assertTrue(user.verifyPassword("password"));
    }

    @Test
    void getName() {
        assertEquals(user.getName(), "Xyz");
    }

    @Test
    void setName() {
        user.setName("Abc");
        assertEquals(user.getName(), "Abc");
    }
}
