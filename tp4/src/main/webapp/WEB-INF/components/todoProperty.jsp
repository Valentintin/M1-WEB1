<%--
  Created by IntelliJ IDEA.
  User: valou
  Date: 18/10/2023
  Time: 16:19
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<c:set var="todo" value="${requestScope.todoDto}" scope="request"/>
<html>
<head>
    <title>Todo property</title>
    <c:if test="${todo.title != null}">
        <li>Title : ${todo.title}</li>
    </c:if>

    <c:if test="${todo.assignee != null}">
        <li>Assignee : ${todo.assignee}</li>
    </c:if>
</head>
<body>

</body>
</html>
