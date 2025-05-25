<!DOCTYPE html>
<html lang="de">
<head>
    <title>Test</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"><!-- Load d3.js -->
    <script src="https://d3js.org/d3.v4.js"></script>
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
            height: 50px;
            width: 100px;
            min-width: 20px;
            max-width: 100px;
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

<#--<#list 0..10 as x>-->
<#--    <br>-->
<#--</#list>-->

<#--<#if userRank == "admin">-->
<#--    <div>&nbsp; Nur admins können das sehen</div>-->
<#--</#if>-->

<#--<#if userRank == "user">-->
<#--    <div>&nbsp; Nur user können das sehen</div>-->
<#--</#if>-->

<#--<#list 0..10 as x>-->
<#--<br>-->
<#--</#list>-->

<div id="my_dataviz">

</div>

<script>


    var margin = {top: 10, right: 30, bottom: 20, left: 50},
        width = 460 - margin.left - margin.right,
        height = 400 - margin.top - margin.bottom;
    var svg = d3.select("#my_dataviz")
        .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");
    /*
    * Eric: Ich habe doch David gesagt, dass er euch sagen soll dass ihr immer nur
    * abhängig von den ausgewählten filtern ("fractionFilter" queryParameter wird ja in java abgefragt)
    * ENTWEDER das gesamtergebnis, also "totalVotes" sendet, ODER nur das ergebnis einer fraktion
    * Dann schreibt in das attribut "votesBy" von wem die ergebnisse sind
    *
    * Falls es jetzt zu anstrengend ist die query umzuschreiben kannst du halt alternativ
    * in JS alle unnötigen fraktionen rausfiltern.
    */
    let fullData = {
        maximumVotes : 300,             // Eric: gesamtzahl der stimmen von allen / von einer fraktion
        votesBy: "Alle Fraktionen",     // Eric: von wem die stimmen sind
        votes: [                        // Eric: format welches abstimmungen haben können/sollten. Die namen der
            {topic: "Abstimmung über Butterbrote", yes: 200, no: 30, noVotes: 2, abstained: 15},
            {topic: "Abstimmung über Wurstbrote", yes: 50, no: 150, noVotes: 20, abstained: 45}
        ]
    };
    drawStackedBarChart(fullData);

    function drawStackedBarChart(fullData) {

        console.log(fullData);
        let data = fullData.votes;

        if (data.length === 0) { // Eric: wenn das protokoll keine abstimmungen hat gibts logischerweise nichts zu malen
            console.log("Dieses Protokoll hat keine Abstimmungen");
            return;
        }

        // Eric: das "columns" attribut ist ein fieser gemeiner trick vom d3 CSV parser
        // https://stackoverflow.com/questions/64534320/d3-js-data-columns-doesnt-exist
        let subgroups = Object.keys(data[0]);

        // Eric : d.group durch topic ersetzt. d.topic ist z.b. "Abstimmung über Butterbrote",
        // also aus dem array welches in fullData unter votes gespeichert ist
        let groups = d3.map(data, function(d){return(d.topic)}).keys()

        // Add X axis
        let x = d3.scaleBand()
            .domain(groups)
            .range([0, width])
            .padding([0.2])
        svg.append("g")
            .attr("transform", "translate(0," + height + ")")
            .call(d3.axisBottom(x).tickSizeOuter(0));

        // Add Y axis
        let y = d3.scaleLinear()
            .domain([0, fullData.maximumVotes * 1.1]) // Eric: dynamische höhe
            .range([ height, 0 ]);
        svg.append("g")
            .call(d3.axisLeft(y));

        // color palette = one color per subgroup
        let color = d3.scaleOrdinal()
            .domain(subgroups)
            .range(['#e41a1c','#377eb8','#4daf4a','#19483f'])

        //stack the data? --> stack per subgroup
        let stackedData = d3.stack()
            .keys(subgroups)
            (data)

        // Show the bars
        svg.append("g")
            .selectAll("g")
            // Enter in the stack data = loop key per key = group per group
            .data(stackedData)
            .enter().append("g")
            .attr("fill", function(d) { return color(d.key); })
            .selectAll("rect")
            // enter a second time = loop subgroup per subgroup to add all rectangles
            .data(function(d) { return d; })
            .enter().append("rect")
            .attr("x", function(d) { return x(d.data.topic); })   // Eric: d.data.group durch topic ersetzt
            .attr("y", function(d) { return y(d[1]); })
            .attr("height", function(d) { return y(d[0]) - y(d[1]); })
            .attr("width",x.bandwidth())
    }
</script>

<#-- Section testpanels-->

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
