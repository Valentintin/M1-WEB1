/**
 * URL de base de l'API à laquelle seront envoyées les requêtes.
 * Vous pouvez la modifier manuellement pour tester l'application avec une autre API.
 * Vous pouvez réécrire complètement ce fichier dans un script de CI pour que cette valeur corresponde à votre environnement de "production".
 * <strong>Attention :</strong> si vous utilisez une API externe (sur un autre serveur),
 * il faut rajouter l'origine du client dans les paramètres du filtre CORS (fichier <code>web.xml</code> du TP4).
 * @type {string}
 */
const baseUrl = "http://localhost:8080/todos_war/";