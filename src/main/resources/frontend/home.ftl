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
    <div class="home-db-status">
        <div id="db-status">

        </div>
        <div>
            <ul><li><a href="test/">✨ Test-Seite ✨</a></li></ul>
        </div>
    </div>

    <div class="home-left">
        <h1 style="font-size: 3em">Parliament Browser</h1>

        Beschreibung

        <h1><a href="dashboard/">Dashboard</a></h1>

        Beschreibung

        <h1><a href="network/comment/">Redner/Kommentatoren-Netzwerk</a></h1>

        Beschreibung

        <h1><a href="network/speech/">Redner/Themen-Netzwerk</a></h1>

        Beschreibung

        <h1><a href="network/edivio/">Redner/Sentiment/Themen-Netzwerk</a></h1>

        Beschreibung

        <h1><a href="reden/">Reden-Visualisierung</a></h1>

        Beschreibung

        <h1><a href="protokolleditor/">Protokoll-Editor</a></h1>

        Beschreibung

        <h1><a href="latex/">LaTeX-Editor</a></h1>

        Beschreibung

        <h1><a href="loginSite/">Login-Management</a></h1>

        Beschreibung

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
