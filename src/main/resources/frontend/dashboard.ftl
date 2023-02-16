<!DOCTYPE html>
<html lang="de">

<head>
    <meta name="author" content="Andrej Artuschenko, DavidJordan">
    <title> Dashboard </title>

    <#-- Include d3.js for graphs -->
    <script src="https://d3js.org/d3.v7.min.js"></script>

    <#-- Include fontawesome -->
    <link rel="stylesheet" href=
    "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

    <#-- Include jquery for better javascript usage -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>


    <style>
        <#include "css/parliamentBrowser.css">
        <#include "css/dashboard.css">
    </style>
</head>

<body>

<#include "parliamentBrowser.ftl">

<div class="container">

    <div class="navbar">

        <div class="search">

            <input type="text"
                   placeholder=" Suche nach Rednern "
                   id="personInput">
            <input type="text"
                   placeholder=" Suche nach Partei "
                   id="partyInput">
            <input type="text"
                   placeholder=" Suche nach Fraktion "
                   id="fractionInput">
            <button type="button" id="personButt">
                <i class="fa fa-search"
                   style="font-size: 18px;">
                </i>
            </button>

        </div>


        <div class="calenderfield">
            <label for="von">von:</label>
            <input type="date" id="von" name="von">
            <label for="bis">bis:</label>
            <input type="date" id="bis" name="bis">
            <input type="submit" value="Anzeigen" id="submitBtn"/>
        </div>

    </div>
</div>


<div class="main">

    <h1>Dashboard</h1>

    <div id="status-message-box"></div>

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
            <div class="Legend-item">
                <span class="Legend-colorBox" style="background-color: darkorange;"></span>
                <span class="Legend-label">Reden</span>
            </div>
            <div class="Legend-item">
                <span class="Legend-colorBox" style="background-color: navy;"></span>
                <span class="Legend-label">Kommentare</span>
            </div>

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
        // updateCharts();
    });

    const btn = document.getElementById('personButt');

    console.log(btn);

    if (btn) {
        // Not called
        btn.addEventListener('click', () => {
            alert('You clicked the button');
        });
    }

    // Event Listener for the calendar field to trigger the updateCharts function
    document.getElementById("submitBtn").addEventListener("click", updateCharts);

    //Event Listener for the Redner search field to trigger when the search button is clicked
    document.getElementById("personButt").addEventListener("click", updateCharts);


    <#include "js/spiderSentiment.js">
    <#include "js/chart_functions.js">
    <#include  "js/barSpeaker.js">
    <#include "js/pollStackBar.js">

</script>

</html>

