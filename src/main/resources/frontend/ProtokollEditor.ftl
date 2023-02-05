<!DOCTYPE html>
<html lang="de">
<head>
    <title>Parliament Browser Homepage</title>
    <meta name="author" content="Eric Lakhter">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src="https://d3js.org/d3.v7.min.js"></script>
    <style>
        <#include "css/parliamentBrowser.css">
        <#include "css/ProtokollEditor.css">
    </style>
</head>
<body>

<#include "parliamentBrowser.ftl">

<div class="speech-editor">
    <form onsubmit="return false;">
        <label><input type="radio" name="edit-type" value="protocol">Protokoll</label>
        <label><input type="radio" name="edit-type" value="aItem">Tagesordnungspunkt</label>
        <label><input type="radio" name="edit-type" value="speech">Rede</label>
    </form>
    <form onsubmit="parseContent(); return false;" style="height: 100%">
        <label> Editor <button type="submit">Generate</button><br><br>
            <textarea id="editor-textarea"></textarea>
        </label>
    </form>
</div>

<script>
<#include "js/editor.js">
</script>

</body>

</html>
