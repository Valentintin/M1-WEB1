<%--
  Created by IntelliJ IDEA.
  User: valou
  Date: 18/10/2023
  Time: 16:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Todo</title>
    <c:forEach var="todo" items="${requestScope.todoDto}">
    <h1>${todo.title}</h1>
</c:forEach>
</head>
<body>

</body>
</html>
