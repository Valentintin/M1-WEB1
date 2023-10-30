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
<title>Todo</title>
<ul>
    <c:forEach items="${requestScope.todos}" var="todo">
        <li>${todo.title}</li>
    </c:forEach>
</ul>
</body>
</html>
