# Rapport TP7

## 1. Analyse de l'état initial de votre application
déploiement sur Tomcat

script : 
```js
function calc() {
    var timeOrigin = window.performance.timeOrigin;
    var domCompleted = window.performance.timing.domComplete;
    var domLoading = window.performance.timing.domLoading;
    var responseEnd = window.performance.timing.responseEnd;
    var CSSDomEnd = window.performance.timing.domContentLoadedEventEnd;
    var domTotal = domLoading - timeOrigin;
    var HTMLDownloadTime = responseEnd - timeOrigin;
    var CSSDomTime = CSSDomEnd - timeOrigin;
    var CRP = domCompleted - timeOrigin;
    // Calcul pour le App Shell
    var appShell = performance.getEntries().filter(x =>
    x.name.endsWith('.js') || x.name.endsWith('.html') || x.name.endsWith('.css'))[performance.getEntries().filter(x =>
    x.name.endsWith('.js') || x.name.endsWith('.html') || x.name.endsWith('.css')).length - 1].responseEnd;
    console.log("Dom total: " + domTotal);
    console.log("HTML Download time" + HTMLDownloadTime);
    console.log("CSSDom Download time" + CSSDomTime);
    console.log("Appshell time" + appShell);
    console.log("CRP : " + CRP);
    return {
        HTMLDownloadTime: HTMLDownloadTime,
        DomTotal: domTotal,
        CSSDomTime: CSSDomTime,
        AppShellTime: appShell,
        CRP: CRP
    };
}
var results = [];
for (var i = 0; i < 10; i++) {
    results.push(calc());
    location.reload();
}
var averageResults = calculateAverage(results);
console.log("Moyenne des rÃ©sultats : ", averageResults);

function calculateAverage(results) {
    var average = {
    HTMLDownloadTime: 0,
    DomTotal: 0,
    CSSDomTime: 0,
    AppShellTime: 0,
    CRP: 0
    };
    for (var i = 0; i < results.length; i++) {
        average.HTMLDownloadTime += results[i].HTMLDownloadTime;
        average.DomTotal += results[i].DomTotal;
        average.CSSDomTime += results[i].CSSDomTime;
        average.AppShellTime += results[i].AppShellTime;
        average.CRP += results[i].CRP;
    }
    average.HTMLDownloadTime /= results.length;
    average.DomTotal /= results.length;
    average.CSSDomTime /= results.length;
    average.AppShellTime /= results.length;
    average.CRP /= results.length;
    return average;
}
function runMeasurement() {
    var results = JSON.parse(localStorage.getItem('performanceResults'))
    || [];
    results.push(calc());
    localStorage.setItem('performanceResults', JSON.stringify(results));
    location.reload();
}
runMeasurement();
```

| Mesure du... | Resultat en ms |
|--------------|----------------|
| temps de chargement de la page HTML initiale | 129 |
| temps d'affichage de l'app shell | 284.5 |
| temps d'affichage du chemin critique de rendu (CRP) | 291 |

## 2. Déploiement des fichiers statiques sur nginx
déploiement sur nginx

| Mesure du... | Resultat en ms | Amélioration |
|--------------|----------------|--------------|
| temps de chargement de la page HTML initiale | 98.8 | 24% |
| temps d'affichage de l'app shell | 224.7 | 21% |
| temps d'affichage du chemin critique de rendu (CRP) | 236.8 | 19% |

## 3. Optimisation de votre application


voir [notre rapport lighthouse](./rapport_audit.pdf) nous indiquant de très bon score, l'optimisation de l'application n'est pas nécessaire.
