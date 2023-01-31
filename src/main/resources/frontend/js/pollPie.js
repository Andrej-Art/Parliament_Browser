function drawDoughnutChart(target) {
    // set the dimensions and margins of the graph
    const width = 450,
        height = 450,
        margin = 40;

// The radius of the pieplot is half the width or half the height (smallest one). I subtract a bit of margin.
    const radius = Math.min(width, height) / 2 - margin

// append the svg object to the div called 'my_dataviz'
    const svg = d3.select(target)
        .append("svg")
        .attr("width", width)
        .attr("height", height)
        .append("g")
        .attr("transform", 'translate(' + width/2 + ',' + height/2 + ')');

// Create dummy data
    var data = {SPD_JA: 9, SPD_Nein: 20, SPD_Enthaltung:30, CDU_JA:8, CDU_NEIN:12, CDU_Enthaltung:3, FDP_JA:7, FDP_Nein:14}

// set the color scale
    const color = d3.scaleOrdinal()
        .range(d3.schemeSet2);

// Compute the position of each group on the pie:
    const pie = d3.pie()
        .value(function (d) {
            return d[1]
        })
    const data_ready = pie(Object.entries(data))
// Now I know that group A goes from 0 degrees to x degrees and so on.

// shape helper to build arcs:
    const arcGenerator = d3.arc()
        .innerRadius(0)
        .outerRadius(radius)

// Build the pie chart: Basically, each part of the pie is a path that we build using the arc function.
    svg
        .selectAll('mySlices')
        .data(data_ready)
        .join('path')
        .attr('d', arcGenerator)
        .attr('fill', function (d) {
            return (color(d.data[0]))
        })
        .attr("stroke", "black")
        .style("stroke-width", "2px")
        .style("opacity", 0.7)

// Now add the annotation. Use the centroid method to get the best coordinates
    svg
        .selectAll('mySlices')
        .data(data_ready)
        .join('text')
        .text(function (d) {
            return "Partei " + d.data[0]
        })
        .attr("transform", function (d) {
            return 'translate(' + arcGenerator.centroid(d) + ')'
        })
        .style("text-anchor", "middle")
        .style("font-size", 17)
}

