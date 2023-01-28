
/**
 * @author Andrej Artuschenko
 */

function speakerbarchart() {

    const margin = {top: 30, right: 30, bottom: 70, left: 60},
        width = 460 - margin.left - margin.right,
        height = 400 - margin.top - margin.bottom;

    // append the svg object to the body of the page
    const svg = d3.select("#my_dataviz")
        .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");


// Parse the Data
    d3.csv("https://raw.githubusercontent.com/holtzy/data_to_viz/master/Example_dataset/7_OneCatOneNum_header.csv").then(function (data3) {

        // sort data
        data3.sort(function (b, a) {
            return a.Value - b.Value;
        });

        // X axis
        var x = d3.scaleBand()
            .range([ 0, width ])
            .domain(data3.map(function(d) { return d.Country; }))
            .padding(0.2);
        svg.append("g")
            .attr("transform", "translate(0," + height + ")")
            .call(d3.axisBottom(x))
            .selectAll("text")
            .attr("transform", "translate(-10,0)rotate(-45)")
            .style("text-anchor", "end");



        // Add Y axis
        var y = d3.scaleLinear()
            .domain([0, 13000])
            .range([ height, 0]);
        svg.append("g")
            .call(d3.axisLeft(y));

        // Bars
        svg.selectAll("mybar")
            .data(data3)
            .enter()
            .append("rect")
            .attr("x", function(d) { return x(d.Country); })
            .attr("y", function(d) { return y(d.Value); })
            .attr("width", x.bandwidth())
            .attr("height", function(d) { return height - y(d.Value); })
            .attr("fill", "#69b3a2")
            // Adding a tooltip functionality to the chart, changing th opacity
            // when the mouse hovers over
            .on("mouseover", function(d) {
                d3.select(this)
                    .style("opacity", 0.5);
                svg.append("text")
                    .attr("id", "tooltip")
                    .attr("x", x(d.key) + x.bandwidth() / 2)
                    .attr("y", y(d.value) - 5)
                    .text(d.value);
            })
            .on("mouseout", function(d) {
                d3.select(this)
                    .style("opacity", 1);
                d3.select("#tooltip").remove();
            });


    })


}