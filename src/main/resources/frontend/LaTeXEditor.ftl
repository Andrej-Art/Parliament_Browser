<!DOCTYPE html>
<html lang="de">
<head>
    <meta name="author" content="Eric Lakhter">
    <title>LaTeX-Editor</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        <#include "css/parliamentBrowser.css">
        <#include "css/LaTeXEditor.css">
    </style>
</head>
<body>

<#include "parliamentBrowser.ftl">

<div class="editor-container editor-container-textarea">
    <div class="editor-box">
        <form onsubmit="parseLaTeX(); return false;" style="height: 100%">
            <label> Editor <button type="submit">Generate</button><br><br>
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
document.getElementById("status-message-box").innerHTML = new Date().toLocaleDateString('DE') + '<br>Status messages:<br>';
<#include "js/editor.js">
</script>

</body>
</html>
