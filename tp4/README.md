# TP4

## Partie1
Pour commencer l'implémentation des requêtes lié au todo nous nous sommes grandement inspiré du code fournis notamment de UserBusinnesController et UserRessourceController.

## Partie2
Dans cette partie, on a chercher a utiliser des jetons. Les jetons vont permettre de gérer différents la connexion des utilisateurs.

On ne cherche donc plus a stocker les informations dans le serveur mais on veut que le client fournisse ces informations.
C'est pour cela qu'on cherche a supprimer dans notre code tout les getSession.

Nous avons donc choisit de créer depuis UserBusinessController des jetons, ces jetons seront stocké par la suite dans le Header pour qu'il soit systématiquement dans les requêtes.
```java
String token = generateToken(login, todo.findByAssignee(user.getLogin()).stream().map(Todo::hashCode).toList(), request);
```
Le reste ce passe principalement dans le AuthentificationFilter, où on va chercher a vérifier le token. S'il n'est pas valide on enverra une erreur 401, s'il est valide on rajoutera le user dans la requête.
```java
token = token.replace("Bearer ", "");
String login = verifyToken(token, request);
request.setAttribute("token", token);
UserDao userDao = (UserDao) request.getServletContext().getAttribute("userDao");
User user = userDao.findOne(login);
request.setAttribute("user", user);
```
Il faut bien penser a retirer le Bearer avant de procéder a la vérification

En procédant ainsi il suffit maintenant de modifier AuthorizationFilter, où on va récupérer le user mit dans la requête pour vérifier s'il possède les différents droits lié au todo.
```java
url[1].equals(user.getLogin())
```
Il faut également bien penser a générer un nouveau token lors d'un put si on modifie l'assignee.
```java
todo.setAssignee(assignee);
UserDao userDao = (UserDao) request.getServletContext().getAttribute("userDao");
User user = userDao.findOne(todo.getAssignee());
String token = generateToken(user.getLogin(), todoDao.findByAssignee(user.getLogin()).stream().map(Todo::hashCode).toList(), request);
```

## Partie3
Un grand bout de cette partie avait déjà était réalisé lors de la partie1, il nous restait principalement le xml en rajoutant
```xml
<init-param>
    <param-name>cheminVues</param-name>
    <param-value>/WEB-INF/components/</param-value>
</init-param>
<init-param>
    <param-name>suffixeVues</param-name>
    <param-value>.jsp</param-value>
</init-param>
<init-param>
    <param-name>defaultMIME</param-name>
    <param-value>text/html</param-value>
</init-param>
```
 ce qui permet ainsi  depuis le filter de récupérer ces init de cette manière 
 ```java
 config.getInitParameter(defaultMIME)
 ```
 
## Partie4
Dans cette partie, nous avons modifié nos jsp. Nous rajoutons des liens URIs vers les informations souhaité. Cela permettrait par exemple, lorsqu'on affiche les informations d'un user d'avoir des liens qui redirige vers les informations d'un TODO au lieu de possèder seulement l'id.
Par exemple en faisant
```java
<a href="${pageContext.request.contextPath}/todos/${todoHash}">${applicationScope.todoDao.findByHash(todoHash).title}</a>
```
On récupère avec `pageContext.request.contextPath` l'url de base puis on va sur todos/id du todo.
## Partie5