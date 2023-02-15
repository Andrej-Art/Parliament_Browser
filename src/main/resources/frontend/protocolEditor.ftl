<!DOCTYPE html>
<html lang="de">
<head>
    <title>Protokoll-Editor</title>
    <meta name="author" content="Eric Lakhter">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        <#include "css/parliamentBrowser.css">
        <#include "css/protocolEditor.css">
    </style>
</head>
<body>

<#include "parliamentBrowser.ftl">

<div class="protocol-editor">
    <div class="select-box" style="background-color: mediumpurple">
        <div>
            <button onclick="setProtocolEditorButtons()">Protokolle anzeigen</button>
            <button onclick="setPersonEditorButtons()">Personen anzeigen</button>
        </div>
        <ul id="button-list"></ul>
    </div>
    <div class="editor-box" style="background-color: yellowgreen">
        <div id="input-area">

        </div>
    </div>
    <div class="button-box" style="background-color: aquamarine">Editor-Modus:<br>
        <label><input type="radio" onclick="changeLayout()" name="edit-mode" value="protocol" checked="checked">Protokoll</label><br>
        <label><input type="radio" onclick="changeLayout()" name="edit-mode" value="aItem">Tagesordnungspunkt</label><br>
        <label><input type="radio" onclick="changeLayout()" name="edit-mode" value="speech">Rede</label><br>
        <label><input type="radio" onclick="changeLayout()" name="edit-mode" value="person">Person</label><br>
        <div id="checkbox-container">

        </div>
        <div id="submit-container">

        </div>
        <div id="explanation">

        </div>
    </div>
    <div class="status-box" style="background-color: coral">
        <div id="status-message-box"></div>
    </div>
</div>

<script>

<#include "js/editor.js">

permissions = ${permissions};
let protocolAgendaPersonData = ${protocolAgendaPersonData};
let protocols = protocolAgendaPersonData["protocols"];
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
let agendaItems = protocolAgendaPersonData["agendaItems"];
let people = protocolAgendaPersonData["people"];
let personIDs = Object.keys(people);
personIDs.sort((a, b) => parseInt(a) - parseInt(b));

document.getElementById("status-message-box").innerHTML = new Date().toLocaleDateString('DE') + '<br>Status-Benachrichtigungen:<br>' +
    '<span style="color: blue">Achtung! Die DB ist noch nicht wirklich angeschlossen, daher keine Sorgen darum machen dass der Editor behauptet Dinge wurden eingefügt!</span><br>';
document.querySelector('input[value="protocol"]').checked = true;
fillWithData(oldEditMode, cachedProtocol);
setProtocolEditorButtons();

</script>

</body>

</html>
