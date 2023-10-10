<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>User</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<c:set var="user" value="${applicationScope.users.findOne(param.user)}" scope="request"/>
<h2>Utilisateur ${user.login}</h2>
<div>
    Login : ${user.login}<br>
    <form method="post" action="userlist.jsp" target="_parent">
        <label for="name">Prénom : <input type="text" name="name" id="name" value="${user.name}"></label>&nbsp;
        <input type="submit" value="Modification">
        <input type="hidden" name="login" value="${user.login}">
    </form>
    <c:if test="${sessionScope.login.equals(user.login)}">
    <br>
    Todos:
    <ul>
        <c:forEach items="${applicationScope.todos}" var="todo">
            <c:if test="${todo.assignee != null && todo.assignee.equals(user)}">
                <li><a href="todolist#${todo.hashCode()}">${todo.title}</a></li>
            </c:if>
        </c:forEach>
    </ul>
</div>
</c:if>
</body>
</html>