<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

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
<p>Il y a actuellement ${requestScope.users.size()} utilisateur(s) connect&eacute;(s) :</p>
<ul>
    <c:forEach items="${requestScope.users}" var="u">
        <li>${u.login} : <strong><a href="connect?operation=user&user=${u.login}">${u.name}</a></strong></li>
    </c:forEach>
</ul>
</body>
</html>
