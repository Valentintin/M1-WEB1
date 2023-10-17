package fr.univlyon1.m1if.m1if03.controllers;

import fr.univlyon1.m1if.m1if03.dao.UserDao;
import fr.univlyon1.m1if.m1if03.dto.user.UserDtoMapper;
import fr.univlyon1.m1if.m1if03.dto.user.UserResponseDto;
import fr.univlyon1.m1if.m1if03.exceptions.ForbiddenLoginException;
import fr.univlyon1.m1if.m1if03.model.User;
import fr.univlyon1.m1if.m1if03.utils.UrlUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.util.Collection;

/**
 * Contrôleur de ressources "users".<br>
 * Gère les CU liés aux opérations CRUD sur la collection d'utilisateurs :
 * <ul>
 *     <li>Création / modification / suppression d'un utilisateur : POST, PUT, DELETE</li>
 *     <li>Récupération de la liste d'utilisateurs / d'un utilisateur / d'une propriété d'un utilisateur : GET</li>
 * </ul>
 * Cette servlet fait appel à une <i>nested class</i> <code>UserResource</code> qui se charge des appels au DAO.
 * Les opérations métier spécifiques (login &amp; logout) sont réalisées par la servlet <a href="UserBusinessController.html"><code>UserBusinessController</code></a>.
 *
 * @author Lionel Médini
 */
@WebServlet(name = "UserResourceController", urlPatterns = {"/users", "/users/*"})
public class UserResourceController extends HttpServlet {
    private UserDtoMapper userMapper;
    private UserResource userResource;

    //<editor-fold desc="Méthode de gestion du cycle de vie">
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        UserDao userDao = (UserDao) config.getServletContext().getAttribute("userDao");
        userMapper = new UserDtoMapper(config.getServletContext());
        userResource = new UserResource(userDao);
    }
    //</editor-fold>

    //<editor-fold desc="Méthodes de service">
    /**
     * Réalise la création d'un utilisateur.
     * Renvoie un code 201 (Created) en cas de création d'un utilisateur, ou une erreur HTTP appropriée sinon.
     *
     * @param request  Une requête de création, contenant un body avec un login, un password et un nom
     * @param response Une réponse vide, avec uniquement un code de réponse et éventuellement un header <code>Location</code>
     * @throws IOException Voir doc...
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("X-test", "doPost");

        String[] url = UrlUtils.getUrlParts(request);

        if (url.length == 1) {// Création d'un utilisateur
            // TODO Parsing des paramètres "old school" ; sera amélioré dans la partie négociation de contenus...
            String login = request.getParameter("login");
            String password = request.getParameter("password");
            String name = request.getParameter("name");
            try {
                userResource.create(login, password, name);
                response.setHeader("Location", "users/" + login);
                response.setStatus(HttpServletResponse.SC_CREATED);
            } catch (IllegalArgumentException | ForbiddenLoginException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (NameAlreadyBoundException e) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Le login " + login + " n'est plus disponible.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Traite les requêtes GET.
     * Renvoie une représentation de la ressource demandée.
     * <ul>
     *     <li>Soit la liste de tous les utilisateurs</li>
     *     <li>Soit un utilisateur</li>
     *     <li>soit une propriété d'un utilisateur</li>
     *     <li>soit une redirection vers une sous-propriété</li>
     * </ul>
     * Renvoie un code de réponse 200 (OK) en cas de représentation, 302 (Found) en cas de redirection, sinon une erreur HTTP appropriée.
     *
     * @param request  Une requête vide
     * @param response Une réponse contenant :
     *                 <ul>
     *                     <li>la liste des liens vers les instances de <code>User</code> existantes</li>
     *                     <li>les propriétés d'un utilisateur</li>
     *                     <li>une propriété donnée d'un utilisateur donné</li>
     *                 </ul>
     * @throws ServletException Voir doc...
     * @throws IOException      Voir doc...
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("X-test", "doGet");
        String[] url = UrlUtils.getUrlParts(request);
        if (url.length == 1) { // Renvoie la liste de tous les utilisateurs
            request.setAttribute("users", userResource.readAll());
            // Transfère la gestion de l'interface à une JSP
            request.getRequestDispatcher("/WEB-INF/components/users.jsp").include(request, response);
            return;
        }
        try {
            User user = userResource.readOne(url[1]);
            UserResponseDto userDto = userMapper.toDto(user);
            switch (url.length) {
                case 2 -> { // Renvoie un DTO d'utilisateur (avec toutes les infos le concernant pour pouvoir le templater dans la vue)
                    request.setAttribute("userDto", userDto);
                    request.getRequestDispatcher("/WEB-INF/components/user.jsp").include(request, response);
                }
                case 3 -> { // Renvoie une propriété d'un utilisateur
                    switch (url[2]) {
                        case "name" -> {
                            request.setAttribute("userDto", new UserResponseDto(userDto.getLogin(), userDto.getName(), null));
                            request.getRequestDispatcher("/WEB-INF/components/userProperty.jsp").include(request, response);
                        }
                        case "assignedTodos" -> {
                            request.setAttribute("userDto", new UserResponseDto(userDto.getLogin(), null, userDto.getAssignedTodos()));
                            request.getRequestDispatcher("/WEB-INF/components/userProperty.jsp").include(request, response);
                        }
                        default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
                default -> { // Redirige vers l'URL qui devrait correspondre à la sous-propriété demandée (qu'elle existe ou pas ne concerne pas ce contrôleur)
                    if (url[2].equals("assignedTodos")) {
                        // Construction de la fin de l'URL vers laquelle rediriger
                        String urlEnd = UrlUtils.getUrlEnd(request, 3);
                        response.sendRedirect("todos" + urlEnd);
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
            }
        } catch (IllegalArgumentException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (NameNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "L'utilisateur " + url[1] + " n'existe pas.");
        } catch (InvalidNameException ignored) {
            // Ne devrait pas arriver car les paramètres sont déjà des Strings
        }
    }

    /**
     * Réalise la modification d'un utilisateur.
     * En fonction du login passé dans l'URL :
     * <ul>
     *     <li>création de l'utilisateur s'il n'existe pas</li>
     *     <li>Mise à jour sinon</li>
     * </ul>
     * Renvoie un code de statut 204 (No Content) en cas de succès ou une erreur HTTP appropriée sinon.
     *
     * @param request  Une requête dont l'URL est de la forme <code>/users/{login}</code>, et contenant :
     *                 <ul>
     *                     <li>un password (obligatoire en cas de création)</li>
     *                     <li>un nom (obligatoire en cas de création)</li>
     *                 </ul>
     * @param response Une réponse vide (si succès)
     * @throws IOException Voir doc...
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] url = UrlUtils.getUrlParts(request);
        String login = url[1];
        // TODO Parsing des paramètres "old school" ; sera amélioré dans la partie négociation de contenus...
        String password = request.getParameter("password");
        String name = request.getParameter("name");

        if (url.length == 2) {
            try {
                userResource.update(login, password, name);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (IllegalArgumentException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (NameNotFoundException e) {
                try {
                    userResource.create(login, password, name);
                    response.setHeader("Location", "users/" + login);
                    response.setStatus(HttpServletResponse.SC_CREATED);
                } catch (NameAlreadyBoundException ignored) {
                } catch (IllegalArgumentException | ForbiddenLoginException ex) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                }
            } catch (InvalidNameException ignored) {
                // Ne devrait pas arriver car les paramètres sont déjà des Strings
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Réalise l'aiguillage des requêtes DELETE.<br>
     * En clair : appelle simplement l'opération de suppression de l'utilisateur.<br>
     * Renvoie un code 204 (No Content) si succès ou une erreur HTTP appropriée sinon.
     *
     * @param request  Une requête dont l'URL est de la forme <code>/users/{login}</code>
     * @param response Une réponse vide (si succès)
     * @throws IOException Voir doc...
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] url = UrlUtils.getUrlParts(request);
        String login = url[1];
        if (url.length == 2) {
            try {
                userResource.delete(login);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (IllegalArgumentException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (NameNotFoundException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "L'utilisateur " + login + " n'existe pas.");
            } catch (InvalidNameException ignored) {
                // Ne devrait pas arriver car les paramètres sont déjà des Strings
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Nested class qui interagit avec le DAO">
    /**
     * Nested class qui réalise les opérations "simples" (CRUD) de gestion des ressources de type <code>User</code>.
     * Son utilité est surtout de prendre en charge les différentes exceptions qui peuvent être levées par le DAO.
     *
     * @author Lionel Médini
     */
    private static class UserResource {
        private final UserDao userDao;

        /**
         * Constructeur avec une injection du DAO nécessaire aux opérations.
         * @param userDao le DAO d'utilisateurs provenant du contexte applicatif
         */
        UserResource(UserDao userDao) {
            this.userDao = userDao;
        }

        /**
         * Crée un utilisateur et le place dans le DAO.
         *
         * @param login    Login de l'utilisateur à créer
         * @param password Password de l'utilisateur à créer
         * @param name      Nom de l'utilisateur à créer
         * @throws IllegalArgumentException Si le login est null ou vide ou si le password est null
         * @throws NameAlreadyBoundException Si le login existe déjà
         * @throws ForbiddenLoginException Si le login est "login" ou "logout" (ce qui mènerait à un conflit d'URLs)
         */
        public void create(@NotNull String login, @NotNull String password, String name)
                throws IllegalArgumentException, NameAlreadyBoundException, ForbiddenLoginException {
            if (login == null || login.isEmpty()) {
                throw new IllegalArgumentException("Le login ne doit pas être null ou vide.");
            }
            if (password == null) {
                throw new IllegalArgumentException("Le password ne doit pas être null.");
            }
            // Protection contre les valeurs de login qui poseraient problème au niveau des URLs
            if (login.equals("login") || login.equals("logout")) {
                throw new ForbiddenLoginException();
            }
            userDao.add(new User(login, password, name));
        }

        /**
         * Renvoie les IDs de tous les utilisateurs présents dans le DAO.
         *
         * @return L'ensemble des IDs sous forme d'un <code>Set&lt;Serializable&gt;</code>
         */
        public Collection<User> readAll() {
            return userDao.findAll();
        }

        /**
         * Renvoie un utilisateur s'il est présent dans le DAO.
         *
         * @param login Le login de l'utilisateur demandé
         * @return L'instance de <code>User</code> correspondant au login
         * @throws IllegalArgumentException Si le login est null ou vide
         * @throws NameNotFoundException Si le login ne correspond à aucune entrée dans le DAO
         * @throws InvalidNameException Ne doit pas arriver car les clés du DAO user sont des strings
         */
        public User readOne(@NotNull String login) throws IllegalArgumentException, NameNotFoundException, InvalidNameException {
            if (login == null || login.isEmpty()) {
                throw new IllegalArgumentException("Le login ne doit pas être null ou vide.");
            }
            return userDao.findOne(login);
        }

        /**
         * Met à jour un utilisateur en fonction des paramètres envoyés.<br>
         * Si l'un des paramètres est nul ou vide, le champ correspondant n'est pas mis à jour.
         *
         * @param login     Le login de l'utilisateur à mettre à jour
         * @param password Le password à modifier. Ou pas.
         * @param name      Le nom à modifier. Ou pas.
         * @throws IllegalArgumentException Si le login est null ou vide
         * @throws InvalidNameException Ne doit pas arriver car les clés du DAO user sont des strings
         * @throws NameNotFoundException Si le login ne correspond pas à un utilisateur existant
         */
        public void update(@NotNull String login, String password, String name) throws IllegalArgumentException, InvalidNameException, NameNotFoundException {
            User user = readOne(login);
            if (password != null && !password.isEmpty()) {
                user.setPassword(password);
            }
            if (name != null && !name.isEmpty()) {
                user.setName(name);
            }
        }

        /**
         * Supprime un utilisateur dans le DAO.
         *
         * @param login Le login de l'utilisateur à supprimer
         * @throws IllegalArgumentException Si le login est null ou vide
         * @throws NameNotFoundException Si le login ne correspond à aucune entrée dans le DAO
         * @throws InvalidNameException Ne doit pas arriver car les clés du DAO user sont des strings
         */
        public void delete(@NotNull String login) throws IllegalArgumentException, NameNotFoundException, InvalidNameException {
            if (login == null || login.isEmpty()) {
                throw new IllegalArgumentException("Le login ne doit pas être null ou vide.");
            }
            userDao.deleteById(login);
        }
    }
}
