<!DOCTYPE html>
<html lang="de">
<head>
    <title>Test</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        <#include "css/parliamentBrowser.css">

        .container {
            box-sizing: border-box;
            padding: 70px 30px;
            height: 100vh;
            width: 50%;
        }

        .select-stuff {
            float: left;
            background-color: cornflowerblue;
        }

        .panel-stuff {
            position: relative;
            z-index: 5;
            float: right;
            background-color: coral;
        }

        #panel-select-container {
            position: relative;
            height: 50px;
            width: 100%;
            display: flex;
            justify-content: flex-start;
            overflow-x: auto;
            overflow-y: hidden;
        }

        .panel-select {
            box-sizing: border-box;
            background-color: aquamarine;
            border-color: black;
            border-style: solid;
            border-width: 1px;
            min-width: 20px;
            max-width: 100px;
            height: 50px;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .panel-select:hover {
            background-color: red;
        }

        #panel-entity-container {
            position: relative;
            top: 0;
            min-height: 80%;
            width: 100%;
        }

        .panel-entity {
            position: absolute;
            border-color: black;
            border-style: solid;
            border-width: 1px;
            height: 100%;
            width: 100%;
        }

        .panel-entity-content {
            height:100%;
            width: 100%;
            position: relative;
            background-color: white;
        }
    </style>

</head>
<body>

<#include "parliamentBrowser.ftl">

<div class="container select-stuff">
    <form onsubmit="return false;">
        <label><input type="radio" name="panel-type" value="/reden/" checked="checked">Rede-Vis</label>
        <label><input type="radio" name="panel-type" value="/protokolleditor/">Protokoll-Editor</label>
        <label><input type="radio" name="panel-type" value="/latex/">LaTeX-Editor</label>
        <label><input type="radio" name="panel-type" value="/test/">Test</label>
        <label><input type="radio" name="panel-type" value="/dashboard/">Dashboard</label>
    </form><br><br>
    <form onsubmit="addPanel(); return false;" style="height: 100%">
        <label> Create Panel <button type="submit">Generate</button></label>
    </form>
</div>

<div class="container panel-stuff">
    <div id="panel-select-container"></div>
    <div id="panel-entity-container"></div>

</div>

<script>
function addPanel() {
    let query = document.querySelector('input[name="panel-type"]:checked').value;

    for (let panel of document.getElementsByClassName("panel-entity-content")) {
        panel.style["z-index"] = '1';
    }
    document.getElementById("panel-select-container").insertAdjacentHTML("beforeend",
        '<div class="panel-select" onclick="showPanel(\''+ query + '\')">' + query +'</div>');
    document.getElementById("panel-entity-container").insertAdjacentHTML("beforeend",
        '<div class="panel-entity">' +
        '<iframe class="panel-entity-content" id="panel-' + query +'" src="' + query + '" style="z-index: 5"></iframe>' +
        '</div>'
    );
}
function showPanel(id = "") {
    for (let panel of document.getElementsByClassName("panel-entity-content")) {
        if (panel.id === "panel-" + id)
            panel.style["z-index"] = '5';
        else
            panel.style["z-index"] = '1';
    }
}
</script>


</body>
</html>
