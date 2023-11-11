<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<c:set var="todo" value="${requestScope.model}" scope="request"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Todo Property</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <title>Todo property</title>
    <c:if test="${todo.title != null}">
        <li>Title : <a href="${pageContext.request.contextPath}/todos/${todo.hash}/title">${todo.title}</a></li>
    </c:if>
    <c:if test="${todo.assignee != null}">
        <li>Assignee : <a href="${pageContext.request.contextPath}/todos/${todo.hash}/assignee">${todo.assignee}</a></li>
    </c:if>
    <c:if test="${todo.image != null}">
        <li>Image :<a href="${pageContext.request.contextPath}/todos/${todo.hash}/image">${todo.image}</a></li>
    </c:if>
    <c:if test="${todo.hash != null}">
        <li>Hash : <a href="${pageContext.request.contextPath}/todos/${todo.hash}">${todo.hash}</a></li>
    </c:if>
    <c:if test="${todo.completed != null}">
        <li>Completed : <a href="${pageContext.request.contextPath}/todos/${todo.hash}/completed">${todo.completed}</a></li>
    </c:if>
</body>
</html>
