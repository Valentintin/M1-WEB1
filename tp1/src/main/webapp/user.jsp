
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>User</title>
</head>
<body>
    <form method="post" action="todos">
        <jsp:useBean id="user" type="fr.univlyon1.m1if.m1if03.classes.User" beanName="user" scope="session"/>
        <p>
            Modifier votre nom :<br>
            <label>Login : <input type="text" name="login" value="<jsp:getProperty name="user" property="login"/>" readonly></label><br>
            <label>Prénom : <input type="text" name="name" value="<jsp:getProperty name="user" property="name"/>" ></label><br>
            <input type="submit" value="Modifier">
        </p>
    </form>
</body>
</html>
