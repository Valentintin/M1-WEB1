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
<c:set var="todo" value="${requestScope.model}" scope="request"/>

<body>
    <title>Todo</title>
    <ul>
        <li>
            Todo:
            <ul>
                <li>Titre: <a href="${pageContext.request.contextPath}/todos/${todo.hash}/title">${todo.title}</a></li>
                <li>Assignee: <a href="${pageContext.request.contextPath}/todos/${todo.hash}/assignee">${todo.assignee}</a></li>
                <li>Image: <a href="${pageContext.request.contextPath}/todos/${todo.hash}/image">${todo.image}</a></li>
                <li>Completed: <a href="${pageContext.request.contextPath}/todos/${todo.hash}/completed">${todo.completed}</a></li>
                <li>Hash: <a href="${pageContext.request.contextPath}/todos/${todo.hash}">${todo.hash}</a></li>
            </ul>
        </li>
    </ul>
</body>
</html>
