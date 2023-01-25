<!DOCTYPE html>
<html lang="de">


<head>
    <meta name="author" content="Andrej Artuschenko">
    <title> Dashboard </title>
    <#-- Include d3.js for graphs -->
    <script src="https://d3js.org/d3.v6.js"></script>
    <#-- Include ajax -->
    <link rel="stylesheet" href=
    "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">


    <style>
        <#include "css/dashboard.css">
    </style>
</head>



<div class="container">

    <div class="navbar">

        <div class="search">

            <form action="#">
                <input type="text"
                       placeholder=" Suche nach Rednern "
                       name="search">
                <button>
                    <i class="fa fa-search"
                       style="font-size: 18px;">
                    </i>
                </button>
            </form>
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
            <input name="form" type="submit" value="Anzeigen"/>


        </div>
    </div>
</div>

<div class="main">
    <h1>Große Überschrift</h1>
    <h2>Überschrift etwas kleiner</h2>

    <div class="wrapper">
        <div>
            <h3>Tokens as Line Chart</h3>
            <canvas id="myChart" width="500" height="300"></canvas>
        </div>

        <div>
            <h3>POS als vertikaler Bar Chart</h3>
            <canvas id="myChart1" width="500" height="300"></canvas>
        </div>

        <div>
            <h3>Sentiment als Radar Chart</h3>
            <canvas id="myChart2" width="500" height="300"></canvas>
        </div>

        <div>
            <h3>Named Entities als Multiple Line Chart</h3>
            <canvas id="myChart3" width="500" height="300"></canvas>
        </div>


        <div>
            <h3>Redner als Bar Chart</h3>
            <canvas id="myChart5" width="500" height="300"></canvas>
        </div>

        <div>
            <h3>Abstimmungsergebnisse mit Visualisierung von Namen</h3>
            <canvas id="myChart6" width="500" height="300"></canvas>
            <div id="line_chart"></div>

        </div>

        <div class="card mt-3">
            <div id="svg"></div>
        </div>

        <h3 class="text-center mt-3 text-light">This is a line Chart</h3>
        <div class="card mt-3" style="display: flex; align-items: center">
            <div id="svg"></div>
        </div>


    </div>

</div>

<script>
    <#include "js/linetest.js">




</script>
</body>
</html>

