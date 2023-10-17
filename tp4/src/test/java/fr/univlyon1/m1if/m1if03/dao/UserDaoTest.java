package fr.univlyon1.m1if.m1if03.dao;

import fr.univlyon1.m1if.m1if03.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class UserDaoTest {
    private UserDao dao;
    private User toto;
    private User titi;

    @BeforeEach
    void setUp() throws NameAlreadyBoundException {
        dao = new UserDao();
        toto = new User("toto", "password", "Toto");
        titi = new User("titi", "password", "Titi");
        dao.add(toto);
        dao.add(titi);
    }

    @Test
    void add() {
        try {
            assertEquals(dao.add(new User("tata", "password", "Tata")), "tata");
        } catch (NameAlreadyBoundException e) {
            fail("Exception thrown:" + e.getMessage());
        }
        try {
            assertThrows(NameAlreadyBoundException.class, (Executable) dao.add(new User("titi", "password", "Tata")));
        } catch (NameAlreadyBoundException ignored) {
        }
    }

    @Test
    void delete() {
        try {
            dao.delete(toto);
            assert(true);
        } catch (NameNotFoundException e) {
            fail("Exception thrown:" + e.getMessage());
        }
        try {
            dao.delete(new User("tata", "password", "Tata"));
            fail("Should throw NameNotFoundException");
        } catch (NameNotFoundException ignored) {
            assert(true);
        }
    }

    @Test
    void deleteById() {
        try {
            dao.deleteById("toto");
            assert(true);
        } catch (NameNotFoundException | InvalidNameException e) {
            fail("Exception thrown:" + e.getMessage());
        }
        try {
            dao.deleteById("tutu");
            fail("Should throw NameNotFoundException");
        } catch (NameNotFoundException ignored) {
            assert(true);
        } catch (InvalidNameException e) {
            fail("Should throw NameNotFoundException");
        }
    }

    @Test
    void update() {
        User tata = new User("toto", "password", "Tata");
        try {
            dao.update("toto", tata);
            assert(true);
        } catch (InvalidNameException ignored) {
        }
    }

    @Test
    void getId() {
        assertEquals(dao.getId(toto), "toto");
        assertNotEquals(dao.getId(toto), "titi");
        assertNotEquals(dao.getId(titi), "toto");
    }

    @Test
    void findOne() {
        try {
            assertEquals(dao.findOne("toto"), toto);
            assertNotEquals(dao.findOne("toto"), titi);
            assertNotEquals(dao.findOne("titi"), toto);
        } catch (NameNotFoundException | InvalidNameException e) {
            fail("Exception thrown:" + e.getMessage());
        }
        try {
            dao.findOne("tutu");
            fail("Should throw NameNotFoundException");
        } catch (NameNotFoundException | InvalidNameException ignored) {
            assert(true);
        }
    }

    @Test
    void findAll() {
        assertThat(dao.findAll(), hasSize(2));
        assertThat(dao.findAll(), hasItems(toto, titi));
    }

    @Test
    void findByName() {
        try {
            assertEquals(dao.findByName("Toto"), toto);
            assertNotEquals(dao.findByName("Toto"), titi);
            assertNotEquals(dao.findByName("Titi"), toto);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        try {
            dao.findByName("Tutu");
            fail("Should throw NameNotFoundException");
        } catch (NameNotFoundException ignored) {
            assert(true);
        }
    }
}
