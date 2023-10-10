package fr.univlyon1.m1if.m1if03.classes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {
    private final User user = new User("toto", "Xyz");

    @Test
    void getLogin() {
        assertEquals(user.getLogin(), "toto");
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
