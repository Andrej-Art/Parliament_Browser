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
    <div class="button-box"> Editor:<br>
        <label><input type="radio" onclick="changeLayout()" name="edit-mode" value="protocol" checked="checked">Protokoll</label><br>
        <label><input type="radio" onclick="changeLayout()" name="edit-mode" value="aItem">Tagesordnungspunkt</label><br>
        <label><input type="radio" onclick="changeLayout()" name="edit-mode" value="speech">Rede</label><br>
        <label><input type="radio" onclick="changeLayout()" name="edit-mode" value="person">Person</label><br>
        <label><input type="checkbox" id="overwrite-checkbox">Überschreibe Eintrag mit bereits existierender ID?</label>
    </div>
    <div class="status-box">
        <div id="status-message-box"></div>
    </div>
    <div class="editor-box">
        <label><button tabindex="100" id="generate-button" onclick="parseEditorData()">Generate</button><br><br></label>
        <div id="input-area">

        </div>
    </div>
    <div class="select-box">

    </div>
</div>

<script>
    <#include "js/editor.js">
document.getElementById("status-message-box").innerHTML = new Date().toLocaleDateString('DE') + '<br>Status messages:<br>';
changeLayout();
</script>

</body>

</html>
