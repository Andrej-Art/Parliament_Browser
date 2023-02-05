<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <script src="https://d3js.org/d3.v4.min.js"></script>
    <style><#include "css/network.css"></style>

</head>


<body>
<div id="networkGraph">
    <div class="flex-container">
        <#--    <legend>Fractionfilter</legend>-->

        <div>
            <input type="checkbox" id="SPD" name="SPD" checked onclick="spdCheckbox()">
            <label style="color: red" for="SPD">SPD</label>
        </div>

        <div>
            <input type="checkbox" id="CDU/CSU" name="CDU/CSU" checked onclick="cxuCheckbox()">
            <label style="color: black" for="CDU/CSU">CDU/CSU</label>
        </div>

        <div>
            <input type="checkbox" id="FDP" name="FDP" checked onclick="fdpCheckbox()">
            <label style="color: yellow" for="FDP">FDP</label>
        </div>
        <div>
            <input type="checkbox" id="Grünen" name="Grünen" checked onclick="grueneCheckbox()">
            <label style="color:green" for="Grünen">BÜNDNIS90/Die Grünen</label>
        </div>
        <div>
            <input type="checkbox" id="AfD" name="AfD" checked onclick="afdCheckbox()">
            <label style="color: blue" for="AfD">AfD</label>
        </div>
        <div>
            <input type="checkbox" id="DIE LINKE." name="DIE LINKE." checked onclick="linkeCheckbox()">
            <label style="color: deeppink" for="DIE LINKE.">DIE LINKE.</label>
        </div>
        <div>
            <input type="checkbox" id="parteilos" name="Parteilos" checked onclick="parteilosCheckbox()">
            <label style="color: gray" for="parteilos">Parteilos</label>
        </div>
    </div>
</div>
<script>
    const width = "1920";
    const height = "1080";


    const sourceRadius = 10;
    const entityRadius = 5;

    let graph = ${networkData}
    // let graph = {
    //     nodes: [{"name": "Eric", "group": 1},
    //         {"name": "David", "group": 2},
    //         {"name": "Edvin", "group": 2},
    //         {"name": "Julian", "group": 2},
    //         {"name": "Andrej", "group": 2},
    //         {"name": "Informatik", "group": 8},
    //         {"name": "Mathe", "group": 8},
    //         {"name": "Physik", "group": 8}],
    //     links: [{"source": "Eric", "target": "Informatik", "value": 1},
    //         {"source": "Edvin", "target": "Mathe", "value": 1},
    //         {"source": "Edvin", "target": "Informatik", "value": 1},
    //         {"source": "David", "target": "Mathe", "value": 1},
    //         {"source": "David", "target": "Physik", "value": 1}]
    // };

    var svg = d3.select("#networkGraph")
        .append("svg")
        .attr("width", width)
        .attr("height", height)
        .call(d3.zoom().on("zoom", function () {
            svg.attr("transform", d3.event.transform)
        }));

    var simulation = d3.forceSimulation()
        .force("link", d3.forceLink().id(function (d) {
            return d.name;
        }))
        .force('charge', d3.forceManyBody()
            .strength(-20000)
            .theta(0.5)
            .distanceMax(500)
        )
        .force('collision', d3.forceCollide().radius(function (d) {
            return d.radius
        }))
        .force("center", d3.forceCenter(document.querySelector("#networkGraph").clientWidth / 2, document.querySelector("#networkGraph").clientHeight / 2));

    var link = svg.append("g")
        .selectAll("line")
        .data(graph.links)
        .enter().append("line")

    link
        .style("stroke", "#aaa");

    var node = svg.append("g")
        .attr("class", "nodes")
        .selectAll("circle")
        .data(graph.nodes)
        .enter().append("circle")
        //I made the article/source nodes larger than the entity nodes
        .attr("r", function (d) {
            return d.group == 8 ? 25 : 5
        })


    node
        .style("fill", function (d) {
            switch (true) {
                case (d.group == 1):
                    return "#0d0c0c";
                case (d.group == 2):
                    return "#E63206";
                case (d.group == 3):
                    return "#e6d806";
                case (d.group == 4):
                    return "#06E614";
                case (d.group == 5):
                    return "#E306E6";
                case (d.group == 6):
                    return "#0654e6";
                case (d.group == 7):
                    return "#fcfcfc";
                case (d.group == 8):
                    return "#6550af";
            }
        })
        .style("fill-opacity", "0.9")
        .style("stroke", "#424242")
        .style("stroke-width", "2px");

    var label = svg.append("g")
        .attr("class", "labels")
        .selectAll("text")
        .data(graph.nodes)
        .enter().append("text")
        .text(function (d) {
            return d.name;
        })
        .attr("class", "label")

    label
        .style("text-anchor", "middle")
        .style("font-size", "10.5px");


    function spdCheckbox() {
        let checkboxSPD = document.getElementById("SPD");

        if (checkboxSPD.checked === false) {
            node.filter(function (d){return d.group === 2}).style("visibility", "hidden");
            label.filter(function (d){return d.group === 2}).style("visibility", "hidden");
            link.filter(function (d){return d.source.group === 2}).style("visibility", "hidden");

        } else {
            node.filter(function (d){return d.group === 2}).style("visibility", "visible");
            label.filter(function (d){return d.group === 2}).style("visibility", "visible");
            link.filter(function (d){return d.source.group === 2}).style("visibility", "visible");
        }
    }
    function cxuCheckbox() {
        let checkboxCxU = document.getElementById("CDU/CSU");

        if (checkboxCxU.checked === false) {
            node.filter(function (d){return d.group === 1}).style("visibility", "hidden");
            label.filter(function (d){return d.group === 1}).style("visibility", "hidden");
            link.filter(function (d){return d.source.group === 1}).style("visibility", "hidden");

        } else {
            node.filter(function (d){return d.group === 1}).style("visibility", "visible");
            label.filter(function (d){return d.group === 1}).style("visibility", "visible");
            link.filter(function (d){return d.source.group === 1}).style("visibility", "visible");
        }
    }
    function fdpCheckbox() {
        let checkboxFDP = document.getElementById("FDP");

        if (checkboxFDP.checked === false) {
            node.filter(function (d){return d.group === 3}).style("visibility", "hidden");
            label.filter(function (d){return d.group === 3}).style("visibility", "hidden");
            link.filter(function (d){return d.source.group === 3}).style("visibility", "hidden");

        } else {
            node.filter(function (d){return d.group === 3}).style("visibility", "visible");
            label.filter(function (d){return d.group === 3}).style("visibility", "visible");
            link.filter(function (d){return d.source.group === 3}).style("visibility", "visible");
        }
    }
    function grueneCheckbox() {
        let checkboxGruene = document.getElementById("Grünen");

        if (checkboxGruene.checked === false) {
            node.filter(function (d){return d.group === 4}).style("visibility", "hidden");
            label.filter(function (d){return d.group === 4}).style("visibility", "hidden");
            link.filter(function (d){return d.source.group === 4}).style("visibility", "hidden");

        } else {
            node.filter(function (d){return d.group === 4}).style("visibility", "visible");
            label.filter(function (d){return d.group === 4}).style("visibility", "visible");
            link.filter(function (d){return d.source.group === 4}).style("visibility", "visible");
        }
    }
    function afdCheckbox() {
        let checkboxAfD = document.getElementById("AfD");

        if (checkboxAfD.checked === false) {
            node.filter(function (d){return d.group === 5}).style("visibility", "hidden");
            label.filter(function (d){return d.group === 5}).style("visibility", "hidden");
            link.filter(function (d){return d.source.group === 5}).style("visibility", "hidden");

        } else {
            node.filter(function (d){return d.group === 5}).style("visibility", "visible");
            label.filter(function (d){return d.group === 5}).style("visibility", "visible");
            link.filter(function (d){return d.source.group === 5}).style("visibility", "visible");
        }
    }
    function linkeCheckbox() {
        let checkboxLinke = document.getElementById("DIE LINKE.");

        if (checkboxLinke.checked === false) {
            node.filter(function (d){return d.group === 6}).style("visibility", "hidden");
            label.filter(function (d){return d.group === 6}).style("visibility", "hidden");
            link.filter(function (d){return d.source.group === 6}).style("visibility", "hidden");

        } else {
            node.filter(function (d){return d.group === 6}).style("visibility", "visible");
            label.filter(function (d){return d.group === 6}).style("visibility", "visible");
            link.filter(function (d){return d.source.group === 6}).style("visibility", "visible");
        }
    }
    function parteilosCheckbox() {
        let checkboxParteilos = document.getElementById("parteilos");

        if (checkboxParteilos.checked === false) {
            node.filter(function (d){return d.group === 7}).style("visibility", "hidden");
            label.filter(function (d){return d.group === 7}).style("visibility", "hidden");
            link.filter(function (d){return d.source.group === 7}).style("visibility", "hidden");

        } else {
            node.filter(function (d){return d.group === 7}).style("visibility", "visible");
            label.filter(function (d){return d.group === 7}).style("visibility", "visible");
            link.filter(function (d){return d.source.group === 7}).style("visibility", "visible");
        }
    }

    function ticked() {
        link
            .attr("x1", function (d) {
                return d.source.x;
            })
            .attr("y1", function (d) {
                return d.source.y;
            })
            .attr("x2", function (d) {
                return d.target.x;
            })
            .attr("y2", function (d) {
                return d.target.y;
            });


        node
            .attr("cx", function (d) {
                return d.x + 5;
            })
            .attr("cy", function (d) {
                return d.y - 3;
            });

        label
            .attr("x", function (d) {
                return d.x;
            })
            .attr("y", function (d) {
                return d.y;
            });
    }

    simulation
        .nodes(graph.nodes)
        .on("tick", ticked);


    simulation.force("link")
        .links(graph.links);
</script>


</body>

</html>