<!DOCTYPE html>
<html lang="de">
<head>
    <title>Rede-Visualisierung</title>
    <meta name="author" content="Eric Lakhter">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src="https://d3js.org/d3.v7.min.js"></script>
    <style>
        <#include "css/parliamentBrowser.css">
        <#include "css/speechVis.css">
    </style>
</head>
<body>

<#include "parliamentBrowser.ftl">

<div class="speech-vis-sidebar">
    <h1 style="padding: 50px 20px 0 20px">Auswahl</h1>
    <div class="speech-vis-sidebar-button-container speech-vis-text-search">
        <form onsubmit="findSpeechIDs(); return false;">
            <label>Volltextsuche in Protokollen<br><input id="text-search" type="text" placeholder="Hier eingeben"></label>
            <button type="submit">Suche</button>
        </form>
    </div>
    <div class="speech-vis-sidebar-button-container">
        <button type="button" onclick="setProtocolButtons()" class="speech-vis-sidebar-button">Protokolle anzeigen</button>
    </div>
    <div class="speech-vis-sidebar-button-container">
        <ul id="button-list"></ul>
    </div>
</div>

<div class="speech-vis-text">
    <div class="speech-header-container">
        <div>
            <h1 id="speech-title"></h1>

            <ul id="speech-header"></ul>
        </div>
        <div id="speaker-pic"></div>
    </div>

    <div id="speech-text">

    </div>
</div>

<div class="speech-vis-legend">
    <div>
        Legende Named Entities:
    </div>
    <div>
        <span class="entity-per">Person</span>
    </div>
    <div>
        <span class="entity-org">Organisation</span>
    </div>
    <div>
        <span class="entity-loc">Ort</span>
    </div>
</div>

<script>

let protocolAgendaData = ${protocolAgendaData};
let protocols = protocolAgendaData["protocols"];
let protocolKeys = Object.keys(protocols);
protocolKeys.sort((a, b) => {
    let keyA = a.split("/");
    let keyB = b.split("/");
    if (keyA[0] === keyB[0]) {
        return parseInt(keyA[1]) - parseInt(keyB[1]);
    } else {
        return parseInt(keyA[0]) - parseInt(keyB[0]);
    }
});
let agendaItems = protocolAgendaData["agendaItems"];
setProtocolButtons();

<#include "js/speechVisMenu.js">

<#include "js/speechVis.js">

</script>

</body>
</html>
