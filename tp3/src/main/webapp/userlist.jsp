<%@ page import="fr.univlyon1.m1if.m1if03.classes.User" %>
<%@ page import="fr.univlyon1.m1if.m1if03.daos.Dao" %>
<%@ page import="javax.naming.InvalidNameException" %>
<%@ page import="javax.naming.NameNotFoundException" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    if(request.getMethod().equals("POST")) {
        try {
            if(!request.getParameter("login").equals(request.getSession(false).getAttribute("login")))
                throw new IllegalAccessException("Vous n'avez pas le droit de modifier l'utilisateur " + request.getParameter("login"));
            User user = ((Dao<User>) config.getServletContext().getAttribute("users")).findOne(request.getParameter("login"));
            user.setName(request.getParameter("name"));
        } catch (NullPointerException | InvalidNameException | NameNotFoundException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Login de l'utilisateur vide ou inexistant: " + request.getParameter("login") + ".");
            return;
        } catch (IllegalAccessException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        }
        // On redirige la totalité de l'interface pour afficher le nouveau nom dans l'interface
        response.sendRedirect("interface.jsp");
    } else {
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>TODOs</title>
    <link rel="stylesheet" href="css/style.css">
    <meta http-equiv="refresh" content="5">
</head>
<body>

<h2>Liste des utilisateurs connectés</h2>
<p>Il y a actuellement ${applicationScope.users.findAll().size()} utilisateur(s) connect&eacute;(s) :</p>
<ul>
    <c:forEach items="${applicationScope.users.findAll()}" var="u">
        <li>${u.login} : <strong><a href="user.jsp?user=${u.login}">${u.name}</a></strong></li>
    </c:forEach>
</ul>
</body>
</html>
<%
    }
%>