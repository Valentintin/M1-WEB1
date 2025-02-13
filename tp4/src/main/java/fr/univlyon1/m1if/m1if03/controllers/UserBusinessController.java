package fr.univlyon1.m1if.m1if03.controllers;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.dao.UserDao;
import fr.univlyon1.m1if.m1if03.dto.user.UserRequestDto;
import fr.univlyon1.m1if.m1if03.model.Todo;
import fr.univlyon1.m1if.m1if03.model.User;
import fr.univlyon1.m1if.m1if03.dto.user.UserDtoMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import java.io.IOException;

import static fr.univlyon1.m1if.m1if03.utils.TodosM1if03JwtHelper.generateToken;

/**
 * Contrôleur d'opérations métier "users".<br>
 * Concrètement : gère les opérations de login et de logout.
 *
 * @author Lionel Médini
 */
@WebServlet(name = "UserBusinessController", urlPatterns = {"/users/login", "/users/logout"})
public class UserBusinessController extends HttpServlet {
    private UserBusiness userBusiness;

    //<editor-fold desc="Méthode de gestion du cycle de vie">
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userBusiness = new UserBusiness(config);
    }
    //</editor-fold>

    //<editor-fold desc="Méthode de service">
    /**
     * Réalise l'opération demandée en fonction de la fin de l'URL de la requête (<code>/users/login</code> ou <code>/users/logout</code>).
     * Renvoie un code HTTP 204 (No Content) en cas de succès.
     * Sinon, renvoie une erreur HTTP appropriée.
     *
     * @param request  Voir doc...
     * @param response Voir doc...
     * @throws IOException Voir doc...
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getRequestURI().endsWith("login")) {
            UserRequestDto requestDto = (UserRequestDto) request.getAttribute("dto");
            if (requestDto.getLogin() != null && !requestDto.getLogin().isEmpty()) {
                try {
                    if (userBusiness.userLogin(requestDto.getLogin(), requestDto.getPassword(), request)) {
                        response.setHeader("Authorization", "Bearer " + request.getAttribute("token"));
                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Les login et mot de passe ne correspondent pas.");
                    }
                } catch (IllegalArgumentException ex) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                } catch (NameNotFoundException e) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "L'utilisateur " + requestDto.getLogin() + " n'existe pas.");
                } catch (InvalidNameException ignored) {
                    // Ne doit pas arriver car les logins des utilisateurs sont des Strings
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else if (request.getRequestURI().endsWith("logout")) {
            userBusiness.userLogout(request);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            // Ne devrait pas arriver mais sait-on jamais...
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Nested class qui interagit avec le DAO">
    /**
     * Nested class qui réalise les opérations de login et de logout d'un utilisateur.<br>
     * Cette classe devra être modifiée pour le passage en authentification stateless.
     *
     * @author Lionel Médini
     */
    private static class UserBusiness {
        private final UserDao userDao;
        private UserDtoMapper userMapper;

        /**
         * Constructeur avec une injection du DAO nécessaire aux opérations.
         * @param config le contexte de la servlet
         */
        UserBusiness(ServletConfig  config) {
            this.userDao = (UserDao) config.getServletContext().getAttribute("userDao");

            this.userMapper = new UserDtoMapper(config.getServletContext());
        }

        /**
         * Réalise l'opération de login d'un utilisateur.
         * &Agrave; condition :
         * <ul>
         *     <li>qu'un login soit présent dans la requête</li>
         *     <li>que ce login corresponde à un utilisateur déjà créé par <code>UserResourceController</code></li>
         *     <li>que le password corresponde à celui de l'utilisateur</li>
         * </ul>
         *
         * @param login    le paramètre "login" de la requête
         * @param password le paramètre "password" de la requête
         * @param request  la requête car il faudra y créer une session uniquement à certaines conditions
         * @return <code>true</code> si les login et password correspondent, <code>false</code> sinon
         * @throws IllegalArgumentException Si le login est null ou vide ou si le password est null
         * @throws InvalidNameException     Ne doit pas arriver car les clés du DAO user sont des strings
         * @throws NameNotFoundException    Si le login ne correspond pas à un utilisateur existant
         */
        public boolean userLogin(@NotNull String login, String password, HttpServletRequest request)
                throws IllegalArgumentException, InvalidNameException, NameNotFoundException {
            if (login == null || login.isEmpty()) {
                throw new IllegalArgumentException("Le login ne doit pas être null ou vide.");
            }
            //request.getAttribute("dto");
            User user = userDao.findOne(login);
            TodoDao todo = (TodoDao) request.getServletContext().getAttribute("todoDao");
            if (user.verifyPassword(password)) {
                // Gestion de la session utilisateur
                String token = generateToken(login, todo.findByAssignee(user.getLogin()).stream().map(Todo::hashCode).toList(), request);
                request.setAttribute("token", token);
                //FAIT au dessus
//                HttpSession session = request.getSession(true);
//                session.setAttribute("user", user);
                 return true;
            } else {
                return false;
            }
        }

        /**
         * Réalise l'opération de logout d'un utilisateur.<br>
         * Renvoie un code HTTP 204 (No Content).<br>
         * En première approximation, ne renvoie pas d'erreur si le client n'était pas logué.
         *
         * @param request  la requête qui contient la session à invalider
         */
        public void userLogout(HttpServletRequest request) {
//            request.getSession().invalidate();
        }
        //</editor-fold>
    }
}
