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
        <#include "css/dashboard.css">
    </style>
</head>

<body>

<div class="container">

    <div class="navbar">

        <div class="search">

            <input type="text"
                   placeholder=" Suche nach Rednern "
                   id="personInput">
            <button type="button" id="personButt">
                <i class="fa fa-search"
                   style="font-size: 18px;">
                </i>
            </button>

        </div>


        <div class="dropdown">
            <button class="dropbtn">Parteien
                <i class="fa fa-caret-down"></i>
            </button>
            <div class="dropdown-content">
                <a href="">SPD</a>
                <a href="">CDU</a>
                <a href="">AfD</a>
                <a href="">FDP</a>
                <a href="">Bündnis 90/Die Grünen</a>
                <a href="">Die Linke</a>
                <a href="">Parteilose/Unabhängige</a>
            </div>
        </div>

        <div class="dropdown">
            <button class="dropbtn">Fraktionen
                <i class="fa fa-caret-down"></i>
            </button>
            <div class="dropdown-content">
                <a href="">CDU/CSU-Fraktion</a>
                <a href="">SPD-Fraktion</a>
                <a href="">FDP-Fraktion</a>
                <a href="">Fraktion Bündnis 90/Die Grünen</a>
                <a href="">Fraktion Die Linke</a>
                <a href="">AfD-Fraktion</a>
                <a href="">Fraktionslose</a>
            </div>
        </div>


        <div class="calenderfield">
            <label for="von">von:</label>
            <input type="date" id="von" name="von">
            <label for="bis">bis:</label>
            <input type="date" id="bis" name="bis">
            <input type="submit"  value="Anzeigen" id="submitBtn"/>
        </div>

    </div>
</div>


<div class="main">
    <h1>Große Überschrift</h1>
    <h2>Überschrift etwas kleiner</h2>

    <div class="wrapper" id="chartContainer">
        <div>
            <h3>POS als vertikaler Bar Chart</h3>
            <div id="pos"></div>
        </div>


        <div>
            <h3>Token als Line Chart</h3>
            <div id="tokenLine"></div>
        </div>

        <div>
            <h3>Sentiment als Radar Chart</h3>
            <div id="spider"></div>
        </div>

        <div>
            <h3>Named Entities als Multiple Line Chart</h3>

            <div id="entitiesMulti"></div>
        </div>


        <div>
            <h3>Redner als Bar Chart</h3>
            <div id="my_dataviz"></div>
        </div>

        <div>
            <h3>Abstimmungsergebnisse mit Visualisierung von Namen</h3>
            <div id="pie"></div>
        </div>

    </div>

</div>

</body>

<script>

    $(document).ready(function () {
        console.log("Im up and running!");
        updateCharts();
    });

    // Event Listener for the calendar field to trigger the updateCharts function
    document.getElementById("submitBtn").addEventListener("click", updateCharts);

    //Event Listener for the Redner search field to trigger when the search button is clicked
    document.getElementById("personButt").addEventListener("click", updateCharts);



    <#include "js/spiderSentiment.js"> // bisher wird in dieser .js die ajax funktion aufgerufen. Liegt es eventuell daran?
    <#include "js/chart_functions.js">
    <#include "js/balkentest.js">
    <#include  "js/barSpeaker.js">
    <#include "js/pollPie.js">


</script>

</html>

