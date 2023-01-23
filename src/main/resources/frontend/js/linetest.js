/**
 * Function that generat es a line chart for the Token coarsePOS values. Takes data of the type:
 * <p>
 *   tokenData = [{"verb": 123}, {"adj": 173}, {"noun": 53}, {"punct": 143}, {"adv": 93}, {"con": 155}, {"nn": 187}];
 * <p>
 * @param data
 * @param target
 */
@Unfinished("Still testing")
function createLineChart(data, target) {

    var margin = {top: 20, right: 20, bottom: 30, left: 50},
        width = 960 - margin.left - margin.right,
        height = 500 - margin.top - margin.bottom;


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


    data.forEach(function(d) {
        var keys = Object.keys(d);
        d.label = keys[0];
        d.value = d[keys[0]];
    });


    x.domain(data.map(function(d) { return d.label; }));
    y.domain([0, d3.max(data, function(d) { return d.value; })]);


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
