/**
 * creates force directed graph where the are only person nodes and the link is who commented on who
 * @param graph
 * @author Edvin Nise
 */
function commentNetwork(graph) {
    const width = "1920";
    const height = "1080";


    let svg = d3.select("#commentNetworkGraph")
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
            .strength(-20000)
            .theta(0.5)
            .distanceMax(500))
        .force('collision', d3.forceCollide().radius(function (d) {
            return d.radius
        }))
        .force("center", d3.forceCenter(document.getElementById("commentNetworkGraph").clientWidth / 2, document.getElementById("commentNetworkGraph").clientHeight / 2));

    link = svg.append("g")
        .selectAll("line")
        .data(graph.links)
        .enter().append("line")

    //colors links based on their sentimentvalue
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

        .attr("r", 20)

    //colors node based on party or rather group value
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

/**
 * updates network and opens it by reloading the page
 * @author Edvin Nise
 */
function updateCommentNetwork() {
    const startDate = document.getElementById("von").value;
    const endDate = document.getElementById("bis").value;
    let url = "/network/comment/?von=" + startDate + "&bis=" + endDate;
    window.location.href = url;

}

