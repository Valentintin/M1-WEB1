<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>User</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<c:set var="user" value="${requestScope.model}" scope="request"/>

<h1>Propriété de l'utilisateur ${user.login}</h1>
<ul>
    <c:if test="${user.name != null}">
        <li>Nom : ${user.name}</li>
    </c:if>
    <c:if test="${user.assignedTodos != null}">
        <li>
            Todos:
            <ul>
                <c:forEach items="${user.assignedTodos}" var="todoHash">
                    <li>
                        <a href="${pageContext.request.contextPath}/todos/${todoHash}">${applicationScope.todoDao.findByHash(todoHash).title}</a>
                    </li>
                </c:forEach>
            </ul>
        </li>
    </c:if>
</ul>
</body>
</html>
