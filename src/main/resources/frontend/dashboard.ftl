<!DOCTYPE html>
<html lang="de">


<head>
    <meta name="author" content="Andrej Artuschenko">
    <title> Dashboard </title>
    <#-- bootstrap import for styling -->
    <link rel="stylesheet" href=
    "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <#-- Include jquery for better javascript usage -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <#-- Include d3.js for graphs -->
    <script src="https://d3js.org/d3.v6.js"></script>

    <style>
        <#include "css/dashboard.css">
    </style>
</head>

<body class="bg-dark">
<div class="header">
    <header>
        <div id="navlist">
            <div id="defineheader">

            <label for="ParteienWahl">Parteien</label>
            <select name="ParteienWahl" id="parteienwahl">
                <option value="cdu">CDU</option>
                <option value="spd">SPD</option>
                <option value="afd">AfD</option>
            </select>


            <label for="FraktionenWahl">Fraktionen</label>
            <select name="FraktionenWahl" id="fraktionenwahl">
                <option value="cdu">CDU</option>
                <option value="spd">SPD</option>
                <option value="afd">AfD</option>
            </select>

                <form action="#">
                    <input type="text"
                           placeholder="Suche nach Personen"
                           name="search">
                    <button>
                        <i class="fa fa-search"
                           style="font-size: 18px;">
                        </i>
                    </button>
                </form>
            </div>
        </div>
    </header>
</div>
</body>
</html>

