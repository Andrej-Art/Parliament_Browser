function speechTopicNetwork(graph) {
    const width = "1920";
    const height = "1080";


// let graph = {
//     nodes: [{"name": "Eric", "group": 1},
//         {"name": "David", "group": 2},
//         {"name": "Edvin", "group": 2},
//         {"name": "Julian", "group": 2},
//         {"name": "Andrej", "group": 3}],
//     // {"name": "Informatik", "group": 8},
//     // {"name": "Mathe", "group": 8},
//     // {"name": "Physik", "group": 8}],
//     links: [{"source": "Eric", "target": "Julian", "sentiment": 1},
//         {"source": "Edvin", "target": "Eric", "sentiment": 0.5},
//         {"source": "Edvin", "target": "Andrej", "sentiment": -0.1},
//         {"source": "David", "target": "Andrej", "sentiment": 0},
//         {"source": "Eric", "target": "Edvin", "sentiment": -0.2},
//         {"source": "Andrej", "target": "Eric", "sentiment": 0}]
// };

    let svg = d3.select("#speechTopicNetworkGraph")
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
            .distanceMax(250))
        .force('collision', d3.forceCollide().radius(function (d) {
            return d.radius
        }))
        .force("center", d3.forceCenter(document.getElementById("speechTopicNetworkGraph").clientWidth / 2, document.getElementById("speechTopicNetworkGraph").clientHeight / 2));

    link = svg.append("g")
        .selectAll("line")
        .data(graph.links)
        .enter().append("line")


    link.style("stroke", function (d) {
        switch (true) {
            case d.sentiment < 0:
                return "#e60f0f"
            case d.sentiment === 0:
                return "#7b7676";
            case d.sentiment > 0:
                return "#33df21"

        }
    });


    node = svg.append("g")
        .attr("class", "nodes")
        .selectAll("circle")
        .data(graph.nodes)
        .enter().append("circle")
        //I made the article/source nodes larger than the entity nodes
        .attr("r", function (d) {
            return d.group === 8 ? 25 : 5
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
        .style("fill-opacity", "0.6")
        .style("stroke", "#424242")
        .style("stroke-width", "2px");

    label = svg.append("g")
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


}

function updateSpeechTopicNetwork() {
    const startDate = document.getElementById("von").value;
    const endDate = document.getElementById("bis").value;
    let url = "/network/edivio/?von=" + startDate + "&bis=" + endDate;
    window.location.href = url;

}