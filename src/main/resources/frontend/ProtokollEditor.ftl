<!DOCTYPE html>
<html lang="de">
<head>
    <title>Protokoll-Editor</title>
    <meta name="author" content="Eric Lakhter">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        <#include "css/parliamentBrowser.css">
        <#include "css/ProtokollEditor.css">
    </style>
</head>
<body>

<#include "parliamentBrowser.ftl">

<div class="protocol-editor">
    <div class="button-box"> Editor:<br>
        <label><input type="radio" onclick="changeDefaultText()" name="edit-mode" value="protocol" checked="checked">Protokoll</label><br>
        <label><input type="radio" onclick="changeDefaultText()" name="edit-mode" value="aItem">Tagesordnungspunkt</label><br>
        <label><input type="radio" onclick="changeDefaultText()" name="edit-mode" value="speech">Rede</label><br>
        <label><input type="checkbox" id="overwrite-checkbox">Überschreibe Eintrag mit bereits existierender ID?</label>
    </div>
    <div class="status-box">
        <div id="status-message-box"></div>
    </div>
    <div class="editor-box">
        <form onsubmit="parseContent(); return false;" style="height: 100%">
            <label><button type="submit">Generate</button><br><br>
                <textarea id="editor-textarea" placeholder=""></textarea>
            </label>
        </form>
    </div>
</div>

<script>
document.getElementById("status-message-box").innerHTML = new Date().toLocaleDateString('DE') + '<br>Status messages:<br>';
changeDefaultText();
<#include "js/editor.js">
</script>

</body>

</html>
