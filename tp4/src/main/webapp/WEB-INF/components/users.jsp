<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Users</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <meta http-equiv="refresh" content="5">
</head>
<body>

<h2>Liste des utilisateurs</h2>
<p>Il y a actuellement ${requestScope.users.size()} utilisateur(s) :</p>
<ul>
    <c:forEach items="${requestScope.users}" var="user">
        <li>${user.login} : <strong><a href="${pageContext.request.contextPath}/users/${user.login}">${user.name}</a></strong></li>
    </c:forEach>
</ul>
</body>
</html>
