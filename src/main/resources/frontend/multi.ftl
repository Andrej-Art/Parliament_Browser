<html lang="de">

<head>
    <title>Multiple Linechart Test</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src = "https://d3js.org/d3.v7.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
</head>

<body>

<form action="/chartdata" method="post">
    <div class="calenderfield">
        <label for="von">von:</label>
        <input type="date" id="von" name="datefilterOne">
        <label for="bis">bis:</label>
        <input type="date" id="bis" name="datefilterTwo">
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
enitityData = ${entities};



let posdata = [];
<#list pos as posObject>
posdata.push(${posObject});
</#list>
console.log(posdata);

<#--let entityData = [];-->
<#--<#list entities as entObject>-->
<#--    entityData.push(${entObject});-->
<#--</#list>-->
<#--console.log(entityData);-->

let entityData = ${entities};

console.log(entityData);

let tokenData = [];
<#list token as tokenObject>
    tokenData.push(${tokenObject});
</#list>
console.log(tokenData);



    $(document).ready(function(){
       MultiLineEntities(entityData, '#multiline');
       createLineChart(tokenData, '#line');
        createBarChart(posdata, '#posBar');
        updateCharts();
    })


</script>

</html>