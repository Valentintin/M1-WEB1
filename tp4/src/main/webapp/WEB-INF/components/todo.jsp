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
<c:set var="todo" value="${requestScope.todoDto}" scope="request"/>

<body>
    <title>Todo</title>
    <ul>
        <li>${todo.title}</li>
        <li>${todo.creator}</li>
    </ul>
</body>
</html>
