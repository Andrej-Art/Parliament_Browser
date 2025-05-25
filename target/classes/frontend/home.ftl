<!DOCTYPE html>
<html lang="de">
<head>
    <title>Parliament Browser Homepage</title>
    <meta name="author" content="Eric Lakhter">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src="https://d3js.org/d3.v7.min.js"></script>
    <style>
        <#include "css/parliamentBrowser.css">
        <#include "css/home.css">
    </style>
</head>
<body>

<#include "parliamentBrowser.ftl">

<div class="home-container">
    <div id="db-status">

    </div>

    <div class="home-left">
        <h1 style="font-size: 3em">Parliament Browser</h1>

        Das hier ist die Implementierung der Abschlussaufgabe <b>PARLIAMENT BROWSER</b> der Gruppe 9_4 für PPR WiSe 22/23.

        <h1><a href="dashboard/">Dashboard</a></h1>

        Zeigt Redner als Bar Chart, POS als vertikalen Bar Chart,
        Named Entities als Multiple Line Chart, Tokens als Line Chart, Abstimmungsergebnisse und das Sentiment als Radar Chart an.<br>
        Filter und Suchbegriffe sind optional.

        <h1><a href="network/comment/">Redner/Kommentatoren-Netzwerk</a></h1>

        Visuelle Darstellung zwischen Rednern und ihren Kommentatoren.

        <h1><a href="network/speech/">Redner/Themen-Netzwerk</a></h1>

        Visuelle Darstellung der Redner und der Themen über die sie gesprochen haben.

        <h1><a href="network/topic/">Redner/Sentiment/Themen-Netzwerk</a></h1>

        Visuelle Darstellung von Reden und der Beziehung zu ihren Themen.

        <h1><a href="reden/">Reden-Visualisierung</a></h1>

        Darstellung von NLP-analysierten Reden.

        <h1><a href="protokolleditor/">Protokoll-Editor</a></h1>

        Editoren-Umgebung zum Anschauen, Einfügen, Ändern und Löschen von Protokollen, Tagesordnungspunkten, Reden und Personen.

        <h1><a href="latex/">LaTeX-Editor</a></h1>

        Ermöglicht den Export von Protokollen im TeX-Format und das Generieren von PDFs.

        <h1><a href="loginSite/">Login-Management</a></h1>

        Seite zum Einloggen und Verwalten von Nutzern.

    </div>
</div>


<script>
document.getElementById("parliament-browser-main-navigation-bar-show").style.display = 'flex';
getDBStatus();
async function getDBStatus () {
    document.getElementById("db-status").innerHTML = 'Auf Antwort von der DB warten... <img src="/loadIcon.gif" alt="" style="vertical-align: middle">';
    let response = await fetch("#", {
        method: 'POST'
    });
    let responseJson = await response.json();
    document.getElementById("db-status").innerHTML =
        '<span style="color: ' + (responseJson.status === "Error" ? "red" : "green")  + '">' + responseJson.details + '</span>';
}
</script>

</body>

</html>
