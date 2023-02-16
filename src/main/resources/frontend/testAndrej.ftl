<!DOCTYPE html>
<html lang="de">

<head>
    <meta name="author" content="Andrej Artuschenko, DavidJordan">
    <title> Dashboard </title>

    <#-- Include d3.js for graphs -->
    <script src="https://d3js.org/d3.v7.min.js"></script>

    <#-- Include ajax -->
    <link rel="stylesheet" href=
    "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

    <#-- Include jquery for better javascript usage -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>


    <style>
        <#include "css/parliamentBrowser.css">


        .container {
            box-sizing: border-box;
            padding: 70px 40px;
            height: 100vh;
            width: 100%;
            background-color: blueviolet;
        }

        .select-stuff {
            height: 20%;
            background-color: lightslategrey;
        }


        .panel-stuff {
            position: relative;
            background-color: white;
            z-index: 5;

        }


        .box {
            display: block;
            color: #f2f2f2;
            text-align: center;
            padding: 5px;
            text-decoration: none;
            font-size: 17px;
        }

        /* styling search bar */
        .search input[type=text] {
            width: 250px;
            height: 25px;
            border-radius: 25px;
            border: none;

        }


        .submitBtn {
            background-color: green;
            color: #f2f2f2;
            padding: 5px 10px;
            font-size: 30px;
            border: none;
            cursor: pointer;
        }

        .tab {
            height: 40px;
            width: 150px;
            padding: 10px;
            display: inline-block;
            background-color: lightgray;
            text-align: center;
            cursor: pointer;
            border: 1px solid gray;
            border-bottom: none;
        }

        .tab.active {
            background-color: royalblue;

        }

        .panel {
            width: 100%;
            height: 1000px;
            background-color: #fff;
            border: 1px solid #000;
            margin: 10px;
            padding: 10px;
            position: relative;
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: rosybrown;
        }

        .panel.active {
            display: block;
        }


        .panel .close {
            position: absolute;
            top: 10px;
            right: 10px;
            cursor: pointer;
        }

        .wrapper {
            display: grid;
            grid-template-columns: 800px 800px;
            column-gap: 50px;
            row-gap: 50px;
            background-color: yellow;
        }

        .panel-entity-content {
            height: 100%;
            width: 100%;
            position: relative;
            background-color: royalblue;
        }

        .tab-container {
            display: flex;
            border-bottom: 1px solid gray;
        }

        .tab {
            padding: 10px;
            cursor: pointer;
        }

        .tab.active {
            background-color: royalblue;
            color: white;
        }

        .panel-container {
            height: 300px;
            border: 1px solid gray;
            overflow: hidden;
        }

        .panel {
            height: 100%;
            width: 100%;
            padding: 10px;
            display: none;
        }

        .panel.active {
            display: block;
        }

        .left-side {
            float: left;
            width: 50%;
            text-align: center;
        }

        .right-side {
            float: right;
            width: 50%;
            text-align: center;
        }

        .card {
            display: inline-block;
            background-color: lightgray;
            padding: 10px 20px;
            margin: 10px;
            border-radius: 5px;
        }

        .card-container {
            display: grid;
            background-color: lightgray;
            grid-template-columns: repeat(2, 1fr);
            grid-template-rows: repeat(3, 1fr);
            gap: 20px;
            padding: 10px 20px;
            margin: 10px;
            border-radius: 5px;
        }

        .card-container.graph-container {
            height: 650px;
        }

        .speakerpic{
            width: 200px;
            height: auto;
        }

        .Legend-colorBox {
            width: 1.5rem;
            height: 1.5rem;
            display: inline-block;
            background-color: blue;
        }




    </style>
</head>
<body>
<#-- our dynamic header -->
<#include "parliamentBrowser.ftl">

<#-- now our search bar for the relevant elements -->

<div class="container select-stuff">
    <div class="box">
        <input type="text"
               placeholder=" Suche nach Rednern "
               id="personInput">
        <input type="text"
               placeholder=" Suche nach Partei "
               id="partyInput">
        <input type="text"
               placeholder=" Suche nach Fraktion "
               id="fractionInput">
        <br><br>

        <label for="von">von:</label>
        <input type="date" id="von" name="von">
        <label for="bis">bis:</label>
        <input type="date" id="bis" name="bis">

        <br><br>

        <form onsubmit="addPanel(); return false;" style="height: 100%">
            <label>
                <button type="submit" id="submitBtn" class="submitBtn">Generate</button>
            </label>
        </form>

    </div>
</div>

<div class="container panel-stuff" id="panel-stuff">
    <!-- das sind die tabs -->
    <div id="panel-select-container"></div>
    <!-- das sind die einzelnen felder, die die information beinhalten-->
    <div id="panel-entity-container">

    </div>
</div>


<#-- our panel -->

</body>

<script>

    $(document).ready(function () {
        console.log("Im up and running!");
        // updateCharts();
    });


    //update charts
    document.getElementById("submitBtn").addEventListener("click", updateCharts);
    //const searchResults = document.getElementById("personInput");

    let panelCount = 0;


    function addPanel() {

        event.preventDefault(); // prevent form submission


        const searchResults = document.getElementById("personInput").value;
        const panelSelectContainer = document.getElementById("panel-select-container");
        const panelEntityContainer = document.getElementById("panel-entity-container");

        // hide all existing panels
        const panelEntities = document.getElementsByClassName("panel-entity-content");
        for (let i = 0; i < panelEntities.length; i++) {
            panelEntities[i].style.display = "none";
        }

        // create new tab
        const panelSelect = document.createElement("div");
        panelSelect.className = "tab";
        panelSelect.innerHTML = "Panel "  + (panelCount +1);
        panelCount++;
        panelSelectContainer.appendChild(panelSelect);


        //add a close button to a tab
        const closeButton = document.createElement("button");
        closeButton.innerText="X";
        closeButton.className = "close-button";
        closeButton.addEventListener("click", function (){
            const panelIndex = Array.from(panelSelectContainer.children).indexOf(panelSelect);
            panelSelect.remove();
            panelEntityContainer.children[panelIndex].remove();
        });

        panelSelect.appendChild(closeButton);

        // create new panel content
        const panelEntity = document.createElement("div");
        panelEntity.className = "panel panel-entity-content";
        panelEntity.style.height = "100%";
        panelEntity.style.width = "100%";
        panelEntity.style.display = "flex";
        panelEntity.style.flexDirection = "row";
        panelEntity.style.justifyContent = "space-between";


        // add the cards to the panel content
        const leftContainer = document.createElement("div");
        leftContainer.style.display = "flex";
        leftContainer.style.flexDirection = "column";
        leftContainer.innerHTML =
            "<div class='card-container graph-container'>" +
            "<h5>POS als vertikaler Bar Chart</h5>" +
            "<svg class='graph'>" +
            "<div id="+'pos'+(panelCount)+ "></div>" +
            "</svg>" +
            "</div>" +

            "<div class='card-container graph-container'>" +
            "<h5>Token als Line Chart</h5>" +
            "<svg class = 'graph'>" +
            "<div id ="+ 'tokenLine'+(panelCount)+"></div>" +
            "</svg>" +
            "</div>" +

            "<div class='card-container graph-container'>" +
            "<h5>Sentiment als Radar Chart</h5>" +
            "<svg class = 'graph'>" +
            "<div id ="+ 'spider'+(panelCount)+"></div>" +
            "</svg>" +
            "<div class='Legend-item'>" +
            "<span class='Legend-colorBox' + style='background-color: darkorange';>" + "</span>" +
            "<span class='Legend-label'>"+"Reden" + "</span>" +
            "<br>" +
            "<span class='Legend-colorBox' + style='background-color: navy';>" + "</span>" +
            "<span class='Legend-label'>"+"Kommentare" + "</span>" +
            "</div>" +
            "</div>";

        const rightContainer = document.createElement("div");
        rightContainer.style.display = "flex";
        rightContainer.style.flexDirection = "column";
        rightContainer.innerHTML =

            "<div class='card-container graph-container'>" +
            "<h5>Redner als Bar Chart</h5>" +
            "<svg class = 'graph'>" +
            "<div id ="+ 'my_dataviz'+(panelCount)+"></div>" +
            "</svg>" +
            "</div>" +

            "<div class='card-container graph-container'>" +
            "<h5>Named Entities als Multiple Line Chart</h5>" +
            "<svg class = 'graph'>" +
            "<div id ="+ 'entitiesMulti'+ +(panelCount)+ "></div>" +
            "</svg>" +
            "</div>" +

            "<div class='card-container graph-container'>" +
            "<h5>Abstimmungsergebnisse</h5>" +
            "<svg class = 'graph'>" +
            "<div id ="+ 'pie'+(panelCount)+"></div>" +
            "</svg>" +
            "</div>";

        panelEntity.appendChild(leftContainer);
        panelEntity.appendChild(rightContainer)

        panelEntity.insertBefore(leftContainer, panelEntity.firstChild);
        panelEntity.insertBefore(rightContainer, panelEntity.firstChild);
        panelEntityContainer.appendChild(panelEntity);

        // show the new panel and select its tab
        panelEntity.style.display = "flex";
        panelEntity.style.flexDirection = "row";
        panelEntity.style.justifyContent = "space-between";
        panelEntity.style.width = "100%";
        panelEntity.style.height = "100%";
        panelSelect.click();

        // add click listener to panel tab
        panelSelect.addEventListener("click", function () {
            showPanel(Array.from(panelSelectContainer.children).indexOf(panelSelect));
        });
    }

    function showPanel(index) {
        const panelEntities = document.getElementsByClassName("panel-entity-content");
        for (let i = 0; i < panelEntities.length; i++) {
            if (i === index) {
                panelEntities[i].style.display = "flex";
                panelEntities[i].style.flexDirection = "row";
                panelEntities[i].style.justifyContent = "space-between";
                panelEntities[i].style.width = "100%";
                panelEntities[i].style.height = "100%";

            } else {
                panelEntities[i].style.display = "none";

            }
        }
    }





    <#include "js/spiderSentiment.js">
    <#include "js/chart_functions.js">
    <#include  "js/barSpeaker.js">
    <#include "js/pollStackBar.js">
</script>