const width = "1920";
const height = "1080";

let graph = ${commentNetworkData}
// let graph = {
//     nodes: [{"name": "Eric", "group": 1},
//         {"name": "David", "group": 2},
//         {"name": "Edvin", "group": 2},
//         {"name": "Julian", "group": 2},
//         {"name": "Andrej", "group": 3}],
//         // {"name": "Informatik", "group": 8},
//         // {"name": "Mathe", "group": 8},
//         // {"name": "Physik", "group": 8}],
//     links: [{"source": "Eric", "target": "Julian", "sentiment": 1},
//         {"source": "Edvin", "target": "Eric", "sentiment": 0.5},
//         {"source": "Edvin", "target": "Andrej", "sentiment": -0.1},
//         {"source": "David", "target": "Andrej", "sentiment": 0},
//         {"source": "Eric", "target": "Edvin", "sentiment": -0.2},
//         {"source": "Andrej", "target": "Eric", "sentiment": 0}]};

// let graph = {
//     nodes: [{"name": "Jan Korte", "group": 5},
//         {"name": "Martin Schulz", "group": 2},
//         {"name": "Michael Grosse-Brömer", "group": 1},
//         {"name": "Dietmar Bartsch", "group": 5},
//         {"name": "Petra Pau", "group": 5},
//         {"name": "Katrin Göring-Eckardt", "group": 4},
//         {"name": "Britta Haßelmann", "group": 4},
//         {"name": "Volker Kauder", "group": 1},
//         {"name": "Christian Lindner", "group": 3},
//         {"name": "Test", "group": 8}],
//     links: [{"source": "Britta Haßelmann", "target": "Carsten Schneider"},
//         {"sentiment": 0.1582, "source": "Jan Korte", "target": "Britta Haßelmann"},
//         {"sentiment": 0, "source": "Christian Lindner", "target": "Bernd Baumann"},
//         {"sentiment": 0, "source": "Petra Pau", "target": "Britta Haßelmann"},
//         {"source": "Michael Grosse-Brömer", "target": "Britta Haßelmann"},
//         {"source": "Jan Korte", "target": "Britta Haßelmann"},
//         {"source": "Martin Schulz", "target": "Bernd Baumann"},
//         {"source": "Volker Kauder", "target": "Carsten Schneider"},
//         {"source": "Katrin Göring-Eckardt", "target": "Carsten Schneider"},
//         {"source": "Dietmar Bartsch", "target": "Britta Haßelmann"}]
// };

 svg = d3.select("#commentNetworkGraph")
    .append("svg")
    .attr("width", width)
    .attr("height", height)
    .call(d3.zoom().on("zoom", function () {
        svg.attr("transform", d3.event.transform)
    }))
    .append("g");

let simulation = d3.forceSimulation()
    .force("link", d3.forceLink().id(function (d) {
        return d.name;
    }))
    .force('charge', d3.forceManyBody()
        .strength(-2000)
        .theta(0.5)
        .distanceMax(200)
    )
    .force('collision', d3.forceCollide().radius(function (d) {
        return d.radius
    }))
    .force("center", d3.forceCenter(document.getElementById("commentNetworkGraph").clientWidth / 2, document.getElementById("commentNetworkGraph").clientHeight / 2));

let link = svg.append("g")
    .selectAll("line")
    .data(graph.links)
    .enter().append("line")
link
    .style("stroke", "#aaa");

// link
//     .style("stroke", "#e60f0f");
// // .style("stroke", function (d) {
// //     switch (true) {
// //         case d.sentiment < 0:
// //             return "#e60f0f"
// //         case d.sentiment === 0:
// //             return "#7b7676";
// //         default:
// //             return "#33DF21"
// //         case d.sentiment > 0:
// //             return "#33df21"
// //
// //     }
// // });



let node = svg.append("g")
    .attr("class", "nodes")
    .selectAll("circle")
    .data(graph.nodes)
    .enter().append("circle")

    .attr("r", function (d) {
        return d.group === 8 ? 25 : 20
    })


node
    .style("fill", function (d) {
        switch (d.group) {
            case 1:
                return "#0d0c0c";
            case 2:
                return "#E63206";
            case 3:
                return "#e6d806";
            case 4:
                return "#06E614";
            case 5:
                return "#E306E6";
            case 6:
                return "#0654e6";
            case 7:
                return "#fcfcfc";
            case 8:
                return "#6550af";
        }
    })
    .style("fill-opacity", "0.9")
    .style("stroke", "#424242")
    .style("stroke-width", "2px");

let label = svg.append("g")
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
        node.filter(function (d) {
            return d.group === 2
        }).style("visibility", "hidden");
        label.filter(function (d) {
            return d.group === 2
        }).style("visibility", "hidden");
        link.filter(function (d) {
            return d.source.group === 2
        }).style("visibility", "hidden");

    } else {
        node.filter(function (d) {
            return d.group === 2
        }).style("visibility", "visible");
        label.filter(function (d) {
            return d.group === 2
        }).style("visibility", "visible");
        link.filter(function (d) {
            return d.source.group === 2
        }).style("visibility", "visible");
    }
}

function cxuCheckbox() {
    let checkboxCxU = document.getElementById("CDU/CSU");

    if (checkboxCxU.checked === false) {
        node.filter(function (d) {
            return d.group === 1
        }).style("visibility", "hidden");
        label.filter(function (d) {
            return d.group === 1
        }).style("visibility", "hidden");
        link.filter(function (d) {
            return d.source.group === 1
        }).style("visibility", "hidden");

    } else {
        node.filter(function (d) {
            return d.group === 1
        }).style("visibility", "visible");
        label.filter(function (d) {
            return d.group === 1
        }).style("visibility", "visible");
        link.filter(function (d) {
            return d.source.group === 1
        }).style("visibility", "visible");
    }
}

function fdpCheckbox() {
    let checkboxFDP = document.getElementById("FDP");

    if (checkboxFDP.checked === false) {
        node.filter(function (d) {
            return d.group === 3
        }).style("visibility", "hidden");
        label.filter(function (d) {
            return d.group === 3
        }).style("visibility", "hidden");
        link.filter(function (d) {
            return d.source.group === 3
        }).style("visibility", "hidden");

    } else {
        node.filter(function (d) {
            return d.group === 3
        }).style("visibility", "visible");
        label.filter(function (d) {
            return d.group === 3
        }).style("visibility", "visible");
        link.filter(function (d) {
            return d.source.group === 3
        }).style("visibility", "visible");
    }
}

function grueneCheckbox() {
    let checkboxGruene = document.getElementById("Grünen");

    if (checkboxGruene.checked === false) {
        node.filter(function (d) {
            return d.group === 4
        }).style("visibility", "hidden");
        label.filter(function (d) {
            return d.group === 4
        }).style("visibility", "hidden");
        link.filter(function (d) {
            return d.source.group === 4
        }).style("visibility", "hidden");

    } else {
        node.filter(function (d) {
            return d.group === 4
        }).style("visibility", "visible");
        label.filter(function (d) {
            return d.group === 4
        }).style("visibility", "visible");
        link.filter(function (d) {
            return d.source.group === 4
        }).style("visibility", "visible");
    }
}

function afdCheckbox() {
    let checkboxAfD = document.getElementById("AfD");

    if (checkboxAfD.checked === false) {
        node.filter(function (d) {
            return d.group === 5
        }).style("visibility", "hidden");
        label.filter(function (d) {
            return d.group === 5
        }).style("visibility", "hidden");
        link.filter(function (d) {
            return d.source.group === 5
        }).style("visibility", "hidden");

    } else {
        node.filter(function (d) {
            return d.group === 5
        }).style("visibility", "visible");
        label.filter(function (d) {
            return d.group === 5
        }).style("visibility", "visible");
        link.filter(function (d) {
            return d.source.group === 5
        }).style("visibility", "visible");
    }
}

function linkeCheckbox() {
    let checkboxLinke = document.getElementById("DIE LINKE.");

    if (checkboxLinke.checked === false) {
        node.filter(function (d) {
            return d.group === 6
        }).style("visibility", "hidden");
        label.filter(function (d) {
            return d.group === 6
        }).style("visibility", "hidden");
        link.filter(function (d) {
            return d.source.group === 6
        }).style("visibility", "hidden");

    } else {
        node.filter(function (d) {
            return d.group === 6
        }).style("visibility", "visible");
        label.filter(function (d) {
            return d.group === 6
        }).style("visibility", "visible");
        link.filter(function (d) {
            return d.source.group === 6
        }).style("visibility", "visible");
    }
}

function parteilosCheckbox() {
    let checkboxParteilos = document.getElementById("parteilos");

    if (checkboxParteilos.checked === false) {
        node.filter(function (d) {
            return d.group === 7
        }).style("visibility", "hidden");
        label.filter(function (d) {
            return d.group === 7
        }).style("visibility", "hidden");
        link.filter(function (d) {
            return d.source.group === 7
        }).style("visibility", "hidden");

    } else {
        node.filter(function (d) {
            return d.group === 7
        }).style("visibility", "visible");
        label.filter(function (d) {
            return d.group === 7
        }).style("visibility", "visible");
        link.filter(function (d) {
            return d.source.group === 7
        }).style("visibility", "visible");
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