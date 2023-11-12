# TP4

## Partie1
Pour commencer l'implémentation des requêtes lié au todo nous nous sommes grandement inspiré du code fournis notamment de UserBusinnesController et UserRessourceController.
Nous avons également rapidement utilisé le ContentNegotiation qui récupère tout les informations qu'on veut et permet de renvoyer dans le bon format.

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

Dans cette partie nous avons pu récupérer une partie de notre code du TP3 est l'adapté.
Dans un premier temps nous avons choisit, lors de la création d'un todo nous avons décider de rajouter le Hash dans le dto.
```java
requestDto.setHash(todoHash);
```
Maintenant dans le cacheFilter, nous avons créer une Map<Integer, Date>.
Ainsi nous stockons les id des Todo car stocké les titres ne serait pas cohérent parce que on peut avoir deux todo avec le même nom.

Nous découpons ensuite notre code en deux avec les rêquete POST, DELETE et PUT et de l'autre coté les rêquete GET.

Pour le premier cas on va donc devoir attendre que la création d'un todo soit finit, on utilise donc
````java
HttpServletResponse wrapper = new BufferlessHttpServletResponseWrapper(response);
super.doFilter(request, wrapper, chain);
````
Ce qui nous permet également de redonner la main aux autres filtres. On cherche ensuite a récupérer le todo, soit depuis le TodoRequestDto soit depuis l'url.
Si on récupère un id on le mettra donc dans la map avec la date actuelle. Dans tout les cas on rajoute également a la map pour un id 0 ce qui nous sera utile dans le cas où on veut la liste de todo.

Maintenant pour le GET, on va cherche a comparer la date de la dernière modification
