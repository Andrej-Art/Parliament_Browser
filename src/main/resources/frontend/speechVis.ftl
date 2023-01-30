<!DOCTYPE html>
<html lang="de">
<head>
    <meta name="author" content="Eric Lakhter">
    <title>Rede-Visualisierung</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src="https://d3js.org/d3.v7.min.js"></script>
    <style>
        <#include "css/speechVis.css">
    </style>
</head>
<body>
<div class="speech-vis-sidebar">
    <h1 style="padding: 0 20px">Auswahl</h1>
    <div class="speech-vis-sidebar-button-container">
        <button type="button" onclick="setProtocolButtons()" class="speech-vis-sidebar-buttons">Protokolle anzeigen</button>
    </div>
    <div class="speech-vis-sidebar-button-container">
        <ul id="button-list" style="margin-left: -10px; margin-top: -10px"></ul>
    </div>
</div>
<div class="speech-vis-display">

    <h1 id="speech-title"></h1>

    <ul id="speech-header"></ul>

    <div style="text-align: justify">
        <p id="speech-text"></p>
    </div>

</div>
<script>
    let protocolAgendaData = ${protocolAgendaData};
    let protocols = protocolAgendaData["protocols"];
    let protocolKeys = Object.keys(protocols);
    protocolKeys.sort((a, b) => {return parseInt(a.replace("/", "")) - parseInt(b.replace("/", ""))});
    let agendaItems = protocolAgendaData["agendaItems"];
    setProtocolButtons();

    <#include "js/speechVisMenu.js">

    <#include "js/speechVis.js">

</script>

</body>
</html>
