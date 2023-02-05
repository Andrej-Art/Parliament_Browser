<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <script src="https://d3js.org/d3.v4.min.js"></script>
    <style><#include "css/network.css"></style>

</head>


<body>
<div id="networkGraph">
    <div id = "flexbox" class="flex-container">
        <#--    <legend>Fractionfilter</legend>-->

        <div class = "box">
            <input type="checkbox" id="SPD" name="SPD" checked onclick="spdCheckbox()">
            <label style="color: red" for="SPD">SPD</label>
        </div>

        <div class = "box">
            <input type="checkbox" id="CDU/CSU" name="CDU/CSU" checked onclick="cxuCheckbox()">
            <label style="color: black" for="CDU/CSU">CDU/CSU</label>
        </div>

        <div class = "box">
            <input type="checkbox" id="FDP" name="FDP" checked onclick="fdpCheckbox()">
            <label style="color: yellow" for="FDP">FDP</label>
        </div>
        <div class = "box">
            <input type="checkbox" id="Grünen" name="Grünen" checked onclick="grueneCheckbox()">
            <label style="color:green" for="Grünen">BÜNDNIS90/Die Grünen</label>
        </div>
        <div class = "box">
            <input type="checkbox" id="AfD" name="AfD" checked onclick="afdCheckbox()">
            <label style="color: blue" for="AfD">AfD</label>
        </div>
        <div class = "box">
            <input type="checkbox" id="DIE LINKE." name="DIE LINKE." checked onclick="linkeCheckbox()">
            <label style="color: deeppink" for="DIE LINKE.">DIE LINKE.</label>
        </div>
        <div class = "box">
            <input type="checkbox" id="parteilos" name="Parteilos" checked onclick="parteilosCheckbox()">
            <label style="color: gray" for="parteilos">Parteilos</label>
        </div>
    </div>
</div>
<script>
   <#include "js/redeNetwork.js">
</script>


</body>

</html>