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

function  getName() {
    const login = localStorage.getItem("login");
    const token = localStorage.getItem("token");
    return new Promise((resolve, reject) => {
        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Authorization", token);
        const requestConfig = {
            method: "GET",
            headers: headers,
            mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
        };
        fetch(baseUrl + "users/" + login + "/name", requestConfig)
            .then((response) => {
                if (response.status === 200 && response.headers.get("Content-Type").includes("application/json")) {
                    return response.json();
                } else {
                    throw new Error("Bad response code (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
                }
            }).then((json) => {
            localStorage.setItem("name", json.name);
            resolve(json.name);
        })
            .catch((err) => {
                console.error("In getName: " + err);
            })
    });
}

function getAssignedTodos(){
    const login = localStorage.getItem("login");
    const token = localStorage.getItem("token");
    return new Promise((resolve, reject) => {
        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Authorization", token);
        const requestConfig = {
            method: "GET",
            headers: headers,
            mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
        };
        fetch(baseUrl + "users/" + login + "/assignedTodos", requestConfig)
            .then((response) => {
                if (response.status === 200 && response.headers.get("Content-Type").includes("application/json")) {
                    return response.json();
                } else {
                    throw new Error("Bad response code (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
                }
            }).then((json) => {
                return getInformationForTodos(json.assignedTodos);
            }).then((assignedTodos) => {
                if(assignedTodos != null){
                    resolve(assignedTodos);
                }
            })
            .catch((err) => {
                console.error("In getAssignedTodos: " + err);
            })
    });
}

function getAllTodos(){
    const token = localStorage.getItem("token");
    return new Promise((resolve, reject) => {
        const headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Authorization", token);
        const requestConfig = {
            method: "GET",
            headers: headers,
            mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
        };
        fetch(baseUrl + "todos" , requestConfig)
            .then((response) => {
                if (response.status === 200 && response.headers.get("Content-Type").includes("application/json")) {
                    return response.json();
                } else {
                    throw new Error("Bad response code (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
                }
            }).then((json) => {
            return getInformationForTodos(json);
        }).then((allTodos) => {
            console.log(allTodos);
            if(allTodos != null){
                resolve(allTodos);
            }
        })
            .catch((err) => {
                console.error("In getAllTodos: " + err);
            })
    });
}

function getInformationForTodos(listOfTodos){
    const login = localStorage.getItem("login");
    const token = localStorage.getItem("token");
    return new Promise((resolve, reject) => {
        let compteur = 0; // compteur pour savoir quand envoyer la promesse

        if(listOfTodos.length == 0){
            resolve(listOfTodos);
        }

        const checkCompteur = () => {
            if(compteur == listOfTodos.length){
                if(listOfTodos != null){
                    resolve(listOfTodos);
                }
            }
        }

        for (let i=0;i<listOfTodos.length;i++){
            const headers = new Headers();
            headers.append("Accept", "application/json");
            headers.append("Authorization", token);
            const requestConfig = {
                method: "GET",
                headers: headers,
                mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
            };
            fetch(baseUrl + "todos/" + listOfTodos[i] , requestConfig)
                .then((response) => {
                    if (response.status === 200 && response.headers.get("Content-Type").includes("application/json")) {
                        return response.json();
                    } else {
                        throw new Error("Bad response code (" + response.status + ") or does not contain JSON (" + response.headers.get("Content-Type") + ").");
                    }
                }).then((json) => {
                    listOfTodos[i] = json;
                    listOfTodos[i].userIsAssignee = (login === json.assignee); //savoir si l'utilisateur connecté et celui qui possède le todo
                    compteur++;
                    checkCompteur();
                })
                .catch((err) => {
                    console.error("In getInformationForTodos: " + err);
                })
        }
    });
}

/**
 * Envoie la requête de login en fonction du contenu des champs de l'interface.
 */
function connect() {
    let login = null;
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
                displayConnected(true);
                displayRequestResult("Connexion réussie", "alert-success");
                localStorage.setItem("token", response.headers.get("Authorization"));
                localStorage.setItem("login", login);
                return Promise.all([
                    getName(),
                    getAssignedTodos(),
                    getAllTodos()
                ])
            } else {
                displayRequestResult("Connexion refusée ou impossible", "alert-danger");
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .then(([name, assignedTodos, allTodos]) => {
            renderAll(name, assignedTodos, allTodos);
            location.hash = "#index";
        })
        .catch((err) => {
            console.error("In login: " + err);
        })
}

function createTodo(){
    const login = localStorage.getItem("login");
    const token = localStorage.getItem("token");
    const headers = new Headers();
    headers.append("Content-Type", "application/json");
    headers.append("Authorization", token);
    const body = {
        title: document.getElementById("text").value,
        creator: login,
    };
    const requestConfig = {
        method: "POST",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };
    fetch(baseUrl + "todos", requestConfig)
        .then((response) => {
            if(response.status === 201) {
                displayRequestResult("Todo ajouté", "alert-success");
                return Promise.all([
                    getName(),
                    getAllTodos(),
                    getAssignedTodos()
                ]);
            } else {
                displayRequestResult("Impossible d'ajouté le todo", "alert-danger");
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .then(([name, allTodos, assignedTodos]) => {
            renderAll(name, assignedTodos, allTodos);
        })
        .catch((err) => {
            console.error("In create todo: " + err);
        })
}

function modifyName(){
    const login = localStorage.getItem("login");
    const token = localStorage.getItem("token");
    const headers = new Headers();
    headers.append("Content-Type", "application/json");
    headers.append("Authorization", token);
    const body = {
        name: document.getElementById("nom_update_input").textContent,
    };
    const requestConfig = {
        method: "PUT",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };
    fetch(baseUrl + "users/" + login, requestConfig)
        .then((response) => {
            if(response.status === 204) {
                displayRequestResult("Utilisateur modifié (name)", "alert-success");
                return getName();
            } else {
                displayRequestResult("Impossible de modifier votre nom", "alert-danger");
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .then((name) => {
            renderAll(name, null, null);
        })
        .catch((err) => {
            console.error("In modify Name: " + err);
        })
}

function modifyPassword(){
    const login = localStorage.getItem("login");
    const token = localStorage.getItem("token");
    const headers = new Headers();
    headers.append("Content-Type", "application/json");
    headers.append("Authorization", token);
    const body = {
        password: document.getElementById("password_update_input").value,
    };
    const requestConfig = {
        method: "PUT",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };
    fetch(baseUrl + "users/" + login, requestConfig)
        .then((response) => {
            if(response.status === 204) {
                displayRequestResult("Utilisateur modifié (password)", "alert-success");
            } else {
                displayRequestResult("Impossible de modifier votre password", "alert-danger");
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .catch((err) => {
            console.error("In modify Password: " + err);
        })
}

function changeNameTodo(hash){
    const token = localStorage.getItem("token");
    const headers = new Headers();
    headers.append("Content-Type", "application/json");
    headers.append("Authorization", token);
    const body = {
        title: document.getElementById("nameTodo1").textContent,
    };
    const requestConfig = {
        method: "PUT",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };
    fetch(baseUrl + "todos/" + hash, requestConfig)
        .then((response) => {
            if(response.status === 204) {
                displayRequestResult("Todo name modifié (name)", "alert-success");
                return Promise.all([
                    getName(),
                    getAllTodos(),
                    getAssignedTodos()
                ]);
            } else {
                displayRequestResult("Impossible de modifier le nom", "alert-danger");
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .then(([name, allTodos, assignedTodos]) => {
            renderAll(name, assignedTodos, allTodos);
        })
        .catch((err) => {
            console.error("In modify Name todo: " + err);
        })
}

function  changeAssigneeNull(hash){
    const token = localStorage.getItem("token");
    const headers = new Headers();
    headers.append("Content-Type", "application/json");
    headers.append("Authorization", token);
    const body = {
        assignee: "null",
    };
    const requestConfig = {
        method: "PUT",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };
    console.log(body.assignee);
    fetch(baseUrl + "todos/" + hash, requestConfig)
        .then((response) => {
            //voir pour gérer le cas d'une erreur 201
            if(response.status === 204) {
                displayRequestResult("Todos modifié (Assignee)", "alert-success");
                localStorage.setItem("token", response.headers.get("Authorization"));
                return Promise.all([
                    getName(),
                    getAllTodos(),
                    getAssignedTodos()
                ]);
            } else {
                displayRequestResult("Impossible de modifier l'assignee", "alert-danger");
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .then(([name, allTodos, assignedTodos]) => {
            renderAll(name, assignedTodos, allTodos);
        })
        .catch((err) => {
            console.error("In changeAssignee: " + err);
        })
}

function  changeAssigneeName(hash){
    const token = localStorage.getItem("token");
    const headers = new Headers();
    headers.append("Content-Type", "application/json");
    headers.append("Authorization", token);
    const body = {
        assignee: localStorage.getItem("login"),
    };
    const requestConfig = {
        method: "PUT",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };
    console.log(body.assignee);
    fetch(baseUrl + "todos/" + hash, requestConfig)
        .then((response) => {
            //voir pour gérer le cas d'une erreur 201
            if(response.status === 204) {
                displayRequestResult("Todos modifié (Assignee)", "alert-success");
                localStorage.setItem("token", response.headers.get("Authorization"));
                return Promise.all([
                    getName(),
                    getAllTodos(),
                    getAssignedTodos()
                ]);
            } else {
                displayRequestResult("Impossible de modifier l'assignee", "alert-danger");
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .then(([name, allTodos, assignedTodos]) => {
            renderAll(name, assignedTodos, allTodos);
        })
        .catch((err) => {
            console.error("In changeAssignee: " + err);
        })
}


function changeStatus(hash){
    const token = localStorage.getItem("token");
    const headers = new Headers();
    headers.append("Content-Type", "application/json");
    headers.append("Authorization", token);
    const body = {
        hash: hash,
    };
    const requestConfig = {
        method: "POST",
        headers: headers,
        body: JSON.stringify(body),
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };
    fetch(baseUrl + "todos/toggleStatus", requestConfig)
        .then((response) => {
            if(response.status === 204) {
                displayRequestResult("Statut modifié", "alert-success");
                return Promise.all([
                    getName(),
                    getAllTodos(),
                    getAssignedTodos()
                ]);
            } else {
                displayRequestResult("Impossible de modifié le status du todo", "alert-danger");
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .then(([name, allTodos, assignedTodos]) => {
            renderAll(name, assignedTodos, allTodos);
        })
        .catch((err) => {
            console.error("In changeStatus: " + err);
        })
}

function deleteTodo(hash){
    const token = localStorage.getItem("token");
    const headers = new Headers();
    headers.append("Content-Type", "application/json");
    headers.append("Authorization", token);

    const requestConfig = {
        method: "DELETE",
        headers: headers,
        mode: "cors" // pour le cas où vous utilisez un serveur différent pour l'API et le client.
    };
    fetch(baseUrl + "todos/" + hash, requestConfig)
        .then((response) => {
            //voir pour gérer le cas d'une erreur 201
            if(response.status === 204) {
                displayRequestResult("Todo supprimé", "alert-success");
                return Promise.all([
                    getName(),
                    getAllTodos(),
                    getAssignedTodos()
                ]);
            } else {
                displayRequestResult("Impossible de supprimer le todo", "alert-danger");
                throw new Error("Bad response code (" + response.status + ").");
            }
        })
        .then(([name, allTodos, assignedTodos]) => {
            renderAll(name, assignedTodos, allTodos);
        })
        .catch((err) => {
            console.error("In deleteTodo: " + err);
        })
}

function renderAll(name, assignedTodos, allTodos) {
    const login = localStorage.getItem("login");
    if(name != null) {
        renderTemplate('template_header', { login: login }, 'target_header');
    }
    if(assignedTodos != null && name != null){
        renderTemplate('template_myAccount', { login: login, name: name, todos: assignedTodos }, 'target_myAccount');
    }
    if(allTodos != null && name != null ){
        renderTemplate('template_todoList', { todos: allTodos, name: name, login: login }, 'target_todoList');
    }
}
function deco() {
    // TODO envoyer la requête de déconnexion
    location.hash = "#index";
    displayConnected(false);
}

function majTodos() {
    return Promise.all([
        getName(),
        getAllTodos(),
        getAssignedTodos()
    ])
    .then(([name, allTodos, assignedTodos]) => {
            renderAll(name, assignedTodos, allTodos);
    })
}

setInterval(getNumberOfUsers, 5000);
setInterval(majTodos, 5000);
// </editor-fold>

// Functions pour Templates
function renderTemplate(id, data, idHtml) {
    const template = document.getElementById(id).innerHTML;
    const rendered = Mustache.render(template, data);
    document.getElementById(idHtml).innerHTML = rendered;
}

let loginPopUp;

function displayLogin() {
    let message = "Ton nom est " +  localStorage.getItem("name");

   loginPopUp = window.open("", "MaPopUp", `scrollbars=no,resizable=no,status=no,location=no,toolbar=no,menubar=no,width=200,height=200,left=-1000,top=-1000;`);

    if (loginPopUp == null || typeof(loginPopUp) == 'undefined') {
        displayRequestResult("La fenêtre pop-up a été bloquée. Veuillez autoriser les pop-ups pour ce site.", "alert-danger");
    } else {
        loginPopUp.document.write(`<h1>${message}</h1>`);
    }
}

function hideLogin() {
    if(loginPopUp != null && !loginPopUp.closed) {
        loginPopUp.close();
    }
}