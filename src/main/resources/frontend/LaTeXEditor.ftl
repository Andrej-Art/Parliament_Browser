<!DOCTYPE html>
<html lang="de">
<head>
    <meta name="author" content="Eric Lakhter, DavidJordan">
    <title>LaTeX-Editor</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        <#include "css/parliamentBrowser.css">
        <#include "css/LaTeXEditor.css">
    </style>
</head>
<body>

<#include "parliamentBrowser.ftl">

<div class="pdf-export-sidebar">
    <h1 style="padding: 50px 20px 0 20px">Selection</h1>
    <div class="pdf-export-button-container">
        <button type="button" onclick="createProtocolButtons()" class="pdf-export-protocol-button">Show Protocols
        </button>
    </div>
    <div class="pdf-export-button-container">
        <ul id="button-list" style="margin-left: -10px; margin-top: -10px"></ul>
    </div>
</div>

<div id="status-message-box"></div>

<div class="editor-container editor-container-textarea">
    <div class="editor-box">
        <form onsubmit="parseLaTeX(); return false;" style="height: 50%">
            <label> Editor
                <button type="submit">Generate</button>
                <br><br>
                <textarea id="editor-textarea"></textarea>
            </label>
        </form>
    </div>
    <div class="status-box">
        <div id="status-message-box"></div>
    </div>
</div>

<div class="editor-container editor-container-preview">
    Preview:
    <iframe id="editor-preview">

    </iframe>
</div>

<script>
    let protocolData = ${protocolData};
    let protocols = protocolData["protocols"];
    let protIDs = Object.keys(protocols);

    document.getElementById("status-message-box").innerHTML = new Date().toLocaleDateString('DE') + '<br>Status messages:<br>';
    <#include "js/LaTeX.js">
</script>

</body>
</html>
