<!DOCTYPE html>
<html lang="de">
<head>
    <meta name="author" content="Eric Lakhter">
    <title>LaTeX-Editor</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src="https://d3js.org/d3.v7.min.js"></script>
    <style>
        <#include "css/parliamentBrowser.css">
        <#include "css/LaTeXEditor.css">
    </style>
</head>
<body>

<#include "parliamentBrowser.ftl">

<div class="editor-container editor-container-textarea">
    <form onsubmit="parseLaTeX(); return false;" style="height: 100%">
        <label> Editor <button type="submit">Generate</button><br><br>
            <textarea id="editor-textarea"></textarea>
        </label>
    </form>
</div>

<div class="editor-container editor-container-preview">
    Preview:
    <iframe id="editor-preview">

    </iframe>
</div>

<script>
<#include "js/editor.js">
</script>

</body>
</html>
