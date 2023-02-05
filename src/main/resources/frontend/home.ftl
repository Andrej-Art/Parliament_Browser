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

<div class="homepage-description">
    <h1 id="title">Homepage 🔥</h1>
    <ul>
        <li><a href="test/">✨ Test-Seite ✨</a></li>
        <li><a href="#title">Homepage</a></li>
        <li><a href="dashboard/">Dashboard</a></li>
        <li><a href="network/1/">Redner-Kategorien-Netzwerk</a></li>
        <li><a href="reden/">Reden-Visualisierung</a></li>
        <li><a href="protokolleditor/">Protokoll-Editor</a></li>
        <li><a href="latex/">LaTeX-Editor</a></li>
    </ul>
</div>

<script>
    document.getElementById("parliament-browser-main-navigation-bar").style.display = 'flex';
    for (let button of document.getElementsByClassName("nav-button")) {
        button.style.color = '#bbb';
        button.onclick = '';
    }
</script>

</body>

</html>
