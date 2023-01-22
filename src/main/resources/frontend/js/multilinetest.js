

// The data sets that I cant seem to get this fing js to find from the ftl
let per_data_set = entitiesSon["perEntity"];
let org_data_set = entitiesSon["orgEntity"];
let loc_data_set = entitiesSon["locEntity"];



// set up the scales for the x and y axes
var x = d3.scaleLinear()
    .range([0, width]);

var y = d3.scaleLinear()
    .range([height, 0]);

var color = d3.scaleOrdinal(d3.schemeCategory10);

var line = d3.line()
    .x(function(d) { return x(d.key); })
    .y(function(d) { return y(d.value); });

// create the svg element in the selected div element
var svg = d3.select("#multiline").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

// bind the data to the svg element
var dataset = [per_data_set, org_data_set, loc_data_set];


dataset.forEach(function(data,i){
    svg.append("/multi/")
        .datum(data)
        .attr("class", "line")
        .attr("fill", "none")
        .attr("stroke", color(i))
        .attr("stroke-width", 1.5)
        .attr("d", line);
});

// set the domain for the x and y scales
x.domain([d3.min(dataset, function(c) { return d3.min(c, function(d) { return d.key; }); }),
    d3.max(dataset, function(c) { return d3.max(c, function(d) { return d.key; }); })]);

y.domain([d3.min(dataset, function(c) { return d3.min(c, function(d) { return d.value; }); }),
    d3.max(dataset, function(c) { return d3.max(c, function(d) { return d.value; }); })]);

// add the x and y axis
svg.append("g")
    .attr("transform", "translate(0," + height + ")")
    .call(d3.axisBottom(x));

svg.append("g")
    .call(d3.axisLeft(y));


