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
    <div class="home-left">
        <h1 id="title">Homepage 🔥</h1>
        <ul>
            <li><a href="test/">✨ Test-Seite ✨</a></li>
            <li><a href="#title">Homepage</a></li>
            <li><a href="loginSite/">Login-Management</a></li>
            <li><a href="dashboard/">Dashboard</a></li>
            <li><a href="network/speech/">Redner-Kategorien-Netzwerk</a></li>
            <li><a href="network/comment/">Comment-Speaker-Netzwerk</a></li>
            <li><a href="network/topic/">Rede-Topic-Netzwerk</a></li>
            <li><a href="reden/">Reden-Visualisierung</a></li>
            <li><a href="protokolleditor/">Protokoll-Editor</a></li>
            <li><a href="latex/">LaTeX-Editor</a></li>
        </ul>
    </div>
    <div class="home-right">
        <div id="db-status">

        </div>
    </div>
</div>


<script>
    document.getElementById("parliament-browser-main-navigation-bar-show").style.display = 'flex';
    for (let button of document.getElementsByClassName("nav-button")) {
        button.style.color = '#bbb';
        button.style['text-shadow'] = 'none';
        button.style.cursor = 'default';
        button.onclick = '';
    }
    getDBStatus();
    async function getDBStatus () {
        document.getElementById("db-status").innerHTML = 'Waiting for database response <img src="loadIcon.gif" alt="" style="vertical-align: middle">';
        let response = await fetch("#", {
            method: 'POST'
        });
        let responseJson = await response.json();
        document.getElementById("db-status").innerHTML =
            '<span style="color: ' + (responseJson.status === Error ? "red" : "green")  + '">' + responseJson.details + '</span>';
    }
</script>

</body>

</html>
