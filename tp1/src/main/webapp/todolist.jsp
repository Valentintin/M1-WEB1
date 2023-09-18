<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="fr.univlyon1.m1if.m1if03.classes.Todo" %>
<%@ page import="java.util.List" %>
<%
    List<Todo> todos = (List<Todo>) request.getAttribute("todos");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>TODOs</title>
    <link rel="stylesheet" href="css/style.css">
    <meta http-equiv="refresh" content="5">
</head>
<body>
<table>
    <%
        if (todos != null) {
            for (Todo t : todos) {
    %>
    <form action='todolist' method='POST'>
        <tr>
            <td><%= (t.isCompleted() ? "&#x2611;" : "&#x2610;") %></td>
            <td><em><%= t.getTitle() %></em></td>
            <% if (t.isCompleted()) { %>
            <td><input type='submit' name='toggle' value='Not done!'></td>
            <% } else { %>
            <td><input type='submit' name='toggle' value='Done!'></td>
            <% } %>
            <input type='hidden' name='operation' value='update'>
            <input type='hidden' name='index' value='<%= todos.indexOf(t) %>'>
        </tr>
    </form>
    <%
            }
        }
    %>
</table>
</body>
</html>
