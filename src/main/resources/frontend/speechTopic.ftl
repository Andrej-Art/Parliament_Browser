<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <script src="https://d3js.org/d3.v4.min.js"></script>
    <style><#include "css/network.css"></style>

</head>


<body onload="speechTopicNetwork(data)">

<div id="speechTopicNetworkGraph">
    <div id="flexbox" class="flex-container">
        <#--    <legend>Fractionfilter</legend>-->

        <div class="box">
            <input type="checkbox" id="spd" name="SPD" checked onclick="partyCheckbox('spd')">
            <label style="color: red" for="SPD">SPD</label>
        </div>

        <div class="box">
            <input type="checkbox" id="cxu" name="CDU/CSU" checked onclick="partyCheckbox('cxu')">
            <label style="color: black" for="CDU/CSU">CDU/CSU</label>
        </div>

        <div class="box">
            <input type="checkbox" id="fdp" name="FDP" checked onclick="partyCheckbox('fdp')">
            <label style="color: yellow" for="FDP">FDP</label>
        </div>
        <div class="box">
            <input type="checkbox" id="gruene" name="Grünen" checked onclick="partyCheckbox('gruene')">
            <label style="color:green" for="Grünen">BÜNDNIS90/Die Grünen</label>
        </div>
        <div class="box">
            <input type="checkbox" id="afd" name="AfD" checked onclick="partyCheckbox('afd')">
            <label style="color: blue" for="AfD">AfD</label>
        </div>
        <div class="box">
            <input type="checkbox" id="linke" name="DIE LINKE." checked onclick="partyCheckbox('linke')">
            <label style="color: deeppink" for="DIE LINKE.">DIE LINKE.</label>
        </div>
        <div class="box">
            <input type="checkbox" id="parteilos" name="Parteilos" checked onclick="partyCheckbox('parteilos')">
            <label style="color: gray" for="parteilos">Parteilos</label>
        </div>
        <div class="box">
            <input type="checkbox" id="pos" name="pos" checked onclick="posSentimentCheckbox()">
            <label style="color: green" for="pos">POS</label>
        </div>
        <div class="box">
            <input type="checkbox" id="neg" name="neg" checked onclick="negSentimentCheckbox()">
            <label style="color: red" for="neg">NEG</label>
        </div>
        <div class="box">
            <input type="checkbox" id="neu" name="neu" checked onclick="neuSentimentCheckbox()">
            <label style="color: gray" for="neu">NEU</label>
        </div>
        <div class="calenderfield">
            <label for="von">von:</label>
            <input type="date" id="von" name="von">
            <label for="bis">bis:</label>
            <input type="date" id="bis" name="bis">
            <input type="submit"  value="Anzeigen" id="submitBtn" onclick="updateSpeechTopicNetwork()">
        </div>
    </div>

</div>
<script>
    let data = ${speechTopicNetworkData};
    <#include "js/speechTopicNetwork.js">
    <#include "js/networkFunctions.js">

</script>


</body>

</html>