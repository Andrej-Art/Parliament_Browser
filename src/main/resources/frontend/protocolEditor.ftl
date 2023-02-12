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
    <div class="button-box" style="background-color: aquamarine"> Editor:<br>
        <label><input type="radio" onclick="changeLayout()" name="edit-mode" value="protocol" checked="checked">Protokoll</label><br>
        <label><input type="radio" onclick="changeLayout()" name="edit-mode" value="aItem">Tagesordnungspunkt</label><br>
        <label><input type="radio" onclick="changeLayout()" name="edit-mode" value="speech">Rede</label><br>
        <label><input type="radio" onclick="changeLayout()" name="edit-mode" value="person">Person</label><br>
        <label><input type="checkbox" id="overwrite-checkbox">Überschreibe Eintrag mit bereits existierender ID?</label>
    </div>
    <div class="status-box" style="background-color: coral">
        <div id="status-message-box"></div>
    </div>
    <div class="select-box" style="background-color: cadetblue">

    </div>
    <div class="editor-box" style="background-color: yellowgreen">
        <label><button tabindex="100" id="generate-button" onclick="parseEditorData()">Generate</button><br><br></label>
        <div id="input-area">

        </div>
    </div>
</div>

<script>
    <#include "js/editor.js">

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



document.getElementById("status-message-box").innerHTML = new Date().toLocaleDateString('DE') + '<br>Status messages:<br>';
changeLayout();
</script>

</body>

</html>
