/**
 * Placez ici les scripts qui seront exécutés côté client pour rendre l'application côté client fonctionnelle.
 */

// <editor-fold desc="Gestion de l'affichage">
/**
 * Fait basculer la visibilité des éléments affichés quand le hash change.<br>
 * Passe l'élément actif en inactif et l'élément correspondant au hash en actif.
 * @param hash une chaîne de caractères (trouvée a priori dans le hash) contenant un sélecteur CSS indiquant un élément à rendre visible.
 */
function show(hash) {
    const oldActiveElement = document.querySelector(".active");
    oldActiveElement.classList.remove("active");
    oldActiveElement.classList.add("inactive");
    const newActiveElement = document.querySelector(hash.split("/")[0]);
    newActiveElement.classList.remove("inactive");
    newActiveElement.classList.add("active");
}

/**
 * Affiche pendant 10 secondes un message sur l'interface indiquant le résultat de la dernière opération.
 * @param text Le texte du message à afficher
 * @param cssClass La classe CSS dans laquelle afficher le message (défaut = alert-info)
 */
function displayRequestResult(text, cssClass = "alert-info") {
    const requestResultElement = document.getElementById("requestResult");
    requestResultElement.innerText = text;
    requestResultElement.classList.add(cssClass);
    setTimeout(
        () => {
            requestResultElement.classList.remove(cssClass);
            requestResultElement.innerText = "";
        }, 10000);
}

/**
 * Affiche ou cache les éléments de l'interface qui nécessitent une connexion.
 * @param isConnected un Booléen qui dit si l'utilisateur est connecté ou pas
 */
function displayConnected(isConnected) {
    const elementsRequiringConnection = document.getElementsByClassName("requiresConnection");
    const visibilityValue = isConnected ? "visible" : "collapse";
    for(const element of elementsRequiringConnection) {
        element.style.visibility = visibilityValue;
    }
}

window.addEventListener('hashchange', () => { show(window.location.hash); });
// </editor-fold>

// <editor-fold desc="Gestion des requêtes asynchrones">
/**
 * Met à jour le nombre d'utilisateurs de l'API sur la vue "index".
 */
function getNumberOfUsers() {
    const headers = new Headers();
    headers.append("Accept", "application/json");
    const requestConfig = {
        method: "GET",
        headers: headers,
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };

    fetch(baseUrl + "users", requestConfig)
        .then((response) => {
            if(response.ok && response.headers.get("Content-Type").includes("application/json")) {
                return response.json();
            } else {
                throw new Error("Response is error (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
            }
        }).then((json) => {
            if(Array.isArray(json)) {
                document.getElementById("nbUsers").innerText = json.length;
            } else {
                throw new Error(json + " is not an array.");
            }
        }).catch((err) => {
            console.error("In getNumberOfUsers: " + err);
        });
}

function  getName(login, token) {
    const headers = new Headers();
    headers.append("Content-Type", "application/json");
    headers.append("Authorization", token);
    const requestConfig = {
        method: "GET",
        headers: headers,
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };
    fetch(baseUrl + "users/" + login + "/name", requestConfig)
        .then((response) => {
            if(response.status === 200) {
                console.log(response.json());
                return response.body;
            } else {
                displayRequestResult("Connexion refusée ou impossible", "alert-danger");
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .catch((err) => {
            console.error("In getName: " + err);
        })

}

/**
 * Envoie la requête de login en fonction du contenu des champs de l'interface.
 */
function connect() {
    let token = null;
    let login = null;
    let name = null;
    displayConnected(true);
    const headers = new Headers();
    headers.append("Content-Type", "application/json");
    const body = {
        login: document.getElementById("login_input").value,
        password: document.getElementById("password_input").value
    };
    const requestConfig = {
        method: "POST",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };
    const bodyJSON = JSON.parse(requestConfig.body);
    login = bodyJSON.login;
    fetch(baseUrl + "users/login", requestConfig)
        .then((response) => {
            if(response.status === 204) {
                displayRequestResult("Connexion réussie", "alert-success");
                console.log("In login: Authorization = " + response.headers.get("Authorization"));
                token = response.headers.get("Authorization");
                //token.replace("Bearer ", "");
                name = getName(login, token);
                location.hash = "#index";
            } else {
                displayRequestResult("Connexion refusée ou impossible", "alert-danger");
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .catch((err) => {
            console.error("In login: " + err);
        })

    console.log("Le login : " + login);
    renderTemplate('template_header', { login: login }, 'target_header');
    renderTemplate('template_myAccount', {login : login, name : name , todos: [
            {
                "id": "6874687",
                "name": "todo 1"
            },
            {
                "id": "6546544",
                "name": "todo 2"
            },
            {
                "id": "6546426",
                "name": "todo 3"
            }
        ]
    }, 'target_myAccount');//Attention dans le tableau todos, les elements récup par API se sera de forme todos/48257987413 du coup faudra faire un split pour récup l'id
    //On voudra également récupérer le nom: quand on aura l'id seul On fera une requete pour récup le name
    renderTemplate('template_todoList', {todos: [
            {
                "id": "6874687",
                "name": "todo 1",
                "image": "&#x2611;",
                "assignee": "Bob"
            },
            {
                "id": "6546544",
                "name": "todo 2",
                "image": '&#x2610;',
                "assignee": "Toto"
            },
            {
                "id": "6546426",
                "name": "todo 3",
                "image": "&#x2611;",
                "assignee": "Bob"
            }
        ]
    }, 'target_todoList');
}

function deco() {
    // TODO envoyer la requête de déconnexion
    location.hash = "#index";
    displayConnected(false);
}
setInterval(getNumberOfUsers, 5000);
// </editor-fold>

// Functions pour Templates
function renderTemplate(id, data, idHtml) {
    const template = document.getElementById(id).innerHTML;
    const rendered = Mustache.render(template, data);
    document.getElementById(idHtml).innerHTML = rendered;
}