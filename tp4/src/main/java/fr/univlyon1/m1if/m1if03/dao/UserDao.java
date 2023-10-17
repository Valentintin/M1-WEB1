package fr.univlyon1.m1if.m1if03.dao;

import fr.univlyon1.m1if.m1if03.model.User;
import jakarta.validation.constraints.NotNull;

import javax.naming.NameNotFoundException;
import java.io.Serializable;

/**
 * Implémentation de l'interface DAO pour la classe <code>User</code>.
 *
 * @author Lionel Médini
 */
public class UserDao extends AbstractMapDao<User> {
    //<editor-fold desc="Implémentation de la méthode de la classe générique">
    @Override
    protected Serializable getKeyForElement(@NotNull User element) {
        return element.getLogin();
    }
    //</editor-fold>

    //<editor-fold desc="Méthode métier spécifique au DAO d'utilisateurs">
    /**
     * Renvoie un utilisateur à partir de son nom.<br>
     * Renvoie le premier utilisateur trouvé avec le nom demandé.
     * @param name le nom à rechercher
     * @return un <code>User</code> dont le nom est celui passé en paramètre
     * @throws NameNotFoundException Si le nom de l'utilisateur à rechercher n'a pas été trouvé
     */
    public User findByName(@NotNull String name) throws NameNotFoundException {
        for(User user: this.collection.values()) {
            if(user.getName().equals(name)) {
                return user;
            }
        }
        throw new NameNotFoundException(name);
    }
    //</editor-fold>
}
