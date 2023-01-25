/**
 * Function to generate a Multi_Line_Chart for the Named Entities
 * @param data the query data
 * @param target the   target html element
 * @author DavidJordan
 */
function MultiLineEntities(data, target){

 const originalData = data;

    // Setting the margins
    const margin = {top: 20, right: 20, bottom: 30, left: 50},
        width = 960 - margin.left - margin.right,
        height = 500 - margin.top - margin.bottom;

    // Parsing the date
    const parseDate = d3.timeParse("%Y-%m-%d");

    // Scaling the  x and y axis
    const x = d3.scaleTime()
        .range([0, width]);
    const y = d3.scaleLinear()
        .range([height, 0]);

    // Setting the colors of the respective fields
    const color = d3.scaleOrdinal()
        .domain(["personEntity", "orgEntity", "locationEntity"])
        .range(["red", "blue", "green"]);


    // Setting the axis descriptions
    const xAxis = d3.axisBottom(x)
        .tickFormat(d3.timeFormat("%Y-%m-%d"));
    const yAxis = d3.axisLeft(y);

    const line = d3.line()
        .x(function(d) { return x(d.date); })
        .y(function(d) { return y(d.value); });

    const svg = d3.select(target).append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    const processedData = Object.entries(originalData).map(d => {
        return {
            date: parseDate(d[0]),
            personEntity: d[1].personEntity,
            orgEntity: d[1].orgEntity,
            locationEntity: d[1].locationEntity,
        }
    });

    color.domain(Object.keys(processedData[0]).filter(function(key) { return key !== "date"; }));

    const types = color.domain().map(function(name) {
        return {
            name: name,
            values: processedData.map(function(d) {
                return {date: d.date, value: +d[name]};
            })
        };
    });

    x.domain(d3.extent(processedData, function(d) { return d.date; }));

    y.domain([
        d3.min(types, function(c) { return d3.min(c.values, function(v) { return v.value; }); }),
        d3.max(types, function(c) { return d3.max(c.values, function(v) { return v.value; }); })
    ]);

    svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis);

    svg.append("g")
        .attr("class", "y axis")
        .call(yAxis)
        .append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 6)
        .attr("dy", ".71em")
        .style("text-anchor", "end")
        .text("Values");


    types.forEach(function(type) {
        svg.append("path")
            .attr("class", "line")
            .attr("d", line(type.values))
            .style("fill", "none")
            .style("stroke", color(type.name))
            .style("stroke-width", 2);
    });


    const legend = svg.selectAll(".legend")
        .data(color.domain().slice().reverse())
        .enter().append("g")
        .attr("class", "legend")
        .attr("transform", function(d, i) { return "translate(0," + i * 20 + ")"; });

    legend.append("rect")
        .attr("x", width - 18)
        .attr("width", 18)
        .attr("height", 18)
        .style("fill", color);

    legend.append("text")
        .attr("x", width - 24)
        .attr("y", 9)
        .attr("dy", ".35em")
        .style("text-anchor", "end")
        .text(function(d) { return d; });

}




function createBarChart(data, target) {
    // set the dimensions and margins of the graph
    var margin = {top: 20, right: 20, bottom: 30, left: 40},
        width = 960 - margin.left - margin.right,
        height = 500 - margin.top - margin.bottom;

    // set the x and y scales
    var x = d3.scaleBand()
        .range([0, width])
        .padding(0.1);
    var y = d3.scaleLinear()
        .range([height, 0]);


    var svg = d3.select(target).append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

    // format the data
    data.forEach(function(d) {
        for (var key in d) {
            d.key = key;
            d.value = d[key];
        }
    });

    // Scale the range of the data in the domains
    x.domain(data.map(function(d) { return d.key; }));
    y.domain([0, d3.max(data, function(d) { return d.value; })]);

    // append the rectangles for the bar chart
    svg.selectAll(".bar")
        .data(data)
        .enter().append("rect")
        .attr("class", "bar")
        .attr("x", function(d) { return x(d.key); })
        .attr("width", x.bandwidth())
        .attr("y", function(d) { return y(d.value); })
        .attr("height", function(d) { return height - y(d.value); });

    // add the x Axis
    svg.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x));

    // add the y Axis
    svg.append("g")
        .call(d3.axisLeft(y));
}


/**
 * Function that generat es a line chart for the Token coarsePOS values. Takes data of the type:
 * <p>
 *   tokenData = [{"verb": 123}, {"adj": 173}, {"noun": 53}, {"punct": 143}, {"adv": 93}, {"con": 155}, {"nn": 187}];
 * <p>
 * @param data
 * @param target
 */
function createLineChart(data, target) {
    // setting the margins of the chart
    var margin = {top: 20, right: 20, bottom: 30, left: 50},
        width = 960 - margin.left - margin.right,
        height = 500 - margin.top - margin.bottom;

    // scaling the x and y axis
    var x = d3.scaleBand()
        .range([0, width])
        .padding(0.1);
    var y = d3.scaleLinear()
        .range([height, 0]);

    // Selecting the target html element to insert the svg element
    var svg = d3.select(target).append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

    // Assigning label and value from the dataset
    data.forEach(function(d) {
        var keys = Object.keys(d);
        d.label = keys[0];
        d.value = d[keys[0]];
    });

    // Setting the values which will be assigned to both axis as x-domain and y-domain
    x.domain(data.map(function(d) { return d.label; }));
    y.domain([0, d3.max(data, function(d) { return d.value; })]);

    //Constructing the svg element
    svg.append("path")
        .data([data])
        .attr("class", "line")
        .style("fill", "none")
        .style("stroke", "steelblue")
        .style("stroke-width", "2px")
        .attr("d", d3.line()
            .x(function(d) { return x(d.label) + x.bandwidth()/2; })
            .y(function(d) { return y(d.value); }));


    svg.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x));

    svg.append("g")
        .call(d3.axisLeft(y));
}




