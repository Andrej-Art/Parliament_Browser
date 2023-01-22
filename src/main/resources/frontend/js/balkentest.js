// data = [
//     {name: 'Test1', _id: 23},
//     {name: 'Test2', _id: 40},
//     {name: 'Test3', _id: 50},
// ];

const width = 800;
const height = 400;
const margin = {top:50,bottom:50,left:50,right:50}

var svg = d3.select('#plot1')
    .append('svg')
    .attr('height', height-margin.top-margin.bottom,)
    .attr('width', width-margin.left - margin.right)
    .attr('viewBox', [0,0,width, height]);

const x = d3.scaleBand()
    .domain(d3.range(data.length))
    .range([margin.left, width-margin.right] )
    .padding(0.1);

const y = d3.scaleLinear()
    .domain([0,10])
    .range([height - margin.bottom, margin.top])

svg.append('g')
    .attr('fill', 'steelblue')
    .selectAll('rect')
    .data(data.sort((a,b) => d3.descending(a._id, b._id)))
    .join('rect')
    .attr('x', (d,i) => x(i))
    .attr('y',(d) => y(d._id))
    .attr('height', d => y(0) - y(d._id))
    .attr('width',x.bandwidth())

function xAxis(g) {
    g.attr('transform', `translate(0,` + (height - margin.bottom) + `)`)
        .call(d3.axisBottom(x).tickFormat(i => data[i].name))
        .attr('font-size', '20px')
}

function yAxis(g) {
    g.call(d3.axisLeft(y).ticks(null, data.format))
        .attr('font-size', '20px')
}

svg.append('g').call(yAxis);
svg.append('g').call(xAxis);
svg.node();




