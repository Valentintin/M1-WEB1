package fr.univlyon1.m1if.m1if03.classes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TodoTest {
    private final Todo todo = new Todo("test TODO", "toto");

    @Test
    void testGetTitle() {
        assertEquals(todo.getTitle(), "test TODO");
    }

    @Test
    void testSetTitle() {
        todo.setTitle("new title");
        assertEquals(todo.getTitle(), "new title");
    }

    @Test
    void testIsCompleted() {
        assertFalse(todo.isCompleted());
    }

    @Test
    void testSetCompleted() {
        todo.setCompleted(true);
        assertTrue(todo.isCompleted());
    }

    @Test
    void testEquals() {
        Todo todo2 = new Todo("test todo 2", "titi");
        assertNotEquals(todo, todo2);
    }

    @Test
    void testHashCode() {
        try {
            Integer h = todo.hashCode();
            assertNotEquals(h, 0);
        } catch (Exception e) {
            fail();
        }
    }
}
