<html lang="de">

<head>
    <title>Multiple Linechart Test</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src = "https://d3js.org/d3.v7.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
</head>

<body>

<form onsubmit="updateCharts()">
    <div class="calenderfield">
        <label for="von">von:</label>
        <input type="date" id="von" name="datefil1">
        <label for="bis">bis:</label>
        <input type="date" id="bis" name="datefil2">
        <input type="submit" value="Anzeigen"/>
    </div>
</form>

<h2>Multiline Chart for Named Entities</h2>

<div id="multiline"></div>

<h2>Linechart for Tokens</h2>

<div id ="line"></div>

<h2>Barchart for POS</h2>

<div id ="posBar"></div>



</body>

<script>


<#include "js/chart_functions.js">
<#include "js/linetest.js">


let posdata = [];
<#list pos as posObject>
posdata.push(${posObject});
</#list>

let entityData = ${entities};
let tokenData = [];
<#list token as tokenObject>
    tokenData.push(${tokenObject});
</#list>



    $(document).ready(function() {
        updateCharts();
    })

console.log(entityData)
console.log(posdata)
console.log(tokenData)


</script>

</html>