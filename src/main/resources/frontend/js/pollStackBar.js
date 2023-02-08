
function drawStackedBarChart(data, target) {


    // set the dimensions and margins of the graph
    const margin = {top: 10, right: 30, bottom: 20, left: 50},
        width = 460 - margin.left - margin.right,
        height = 400 - margin.top - margin.bottom;

// append the svg object to the body of the page
    const svg = d3.select(target)
        .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

// Parse the Data
    // d3.csv("https://raw.githubusercontent.com/holtzy/D3-graph-gallery/master/DATA/data_stacked.csv").then( function(data) {


    // Selecting the right fields from the returned JSON object to match the right values



    let apliedData = Object.entries(data).map(d => {
        return {
            AfDresults: d[1].AfDresults,
            B90results: d[1].B90results,
            CxUresults: d[1].CxUresults,
            FDPresults: d[1].FDPresults,
            LINKEresults: d[1].LINKEresults,
            SPDresults: d[1].SPDresults,
            independentresults: d[1].independentresults
        }
    });



    let totalData = Object.entries(data).map(d =>{
        return{
            totalVotes: d[1].totalVotes,
            totalVotesAbstained: d[1].totalVotesAbstained,
            totalVotesNo: d[1].totalVotesNo,
            totalVotesNoVotes: d[1].totalVotesNoVotes,
            totalVotesYes: d[1].totalVotesYes
        }
    })


    // extracting the data and setting the key and value
    // set speakerName as key
    apliedData.forEach(function(d) {
        d.AfDresult = Object.keys(d)[0];
        d.AfDvalues = d[d.AfDresult]

        d.B90results = Object.keys(d)[1];
        d.B90values = d[d.B90results]

        d.CxUresults = Object.keys(d)[2];
        d.CxUvalues = d[d.CxUresults]

        d.FDPresults = Object.keys(d)[3];
        d.FDPvalues = d[d.FDPresults]

        d.LINKEresults = Object.keys(d)[4];
        d.LINKEvalues = d[d.LINKEresults]

        d.SPDresults = Object.keys(d)[5];
        d.SPDvalues = d[d.SPDresults]

        d.independentresults = Object.keys(d)[6];
        d.independentvalues = d[d.independentresults]

    });






    // extracting the data and setting the key and value

    // date=0
    // SPD = 1
    // Indepent = 2
    // totalVotesYes = 3
    // CDU = 4
    // FDP = 5
    // AfD = 6
    // B90 = 7
    // totalVotes = 8
    // totalVotesNo = 9
    // Linke=10
    // pollID = 11
    // totalVotesAbstained = 12
    // totalVotesNoVotes = 13

    // List of subgroups

    /*

    data.forEach(function (d) {
        d.SPD = Object.keys(d)[1];
        d.SPDvalues = d[d.SPD];
        // -------------------------
        d.Independent = Object.keys(d)[1];
        d.IndependentValues = d[d.Independent];
        // -------------------------
        d.CDU = Object.keys(d)[4];
        d.CDUvalues = d[d.CDU];
        // -------------------------
        d.FDP = Object.keys(d)[5];
        d.FDPvalues = d[d.FDP];
        // -------------------------
        d.AfD = Object.keys(d)[6];
        d.AfDvalues = d[d.AfD];
        // -------------------------
        d.B90 = Object.keys(d)[7];
        d.B90values = d[d.B90];
        // -------------------------
        d.Linke = Object.keys(d)[10];
        d.Linkevalues = d[d.Linke];
        // -------------------------
    });
      */

   var fractions = new Array( 'CDU', 'SPD', 'AfD', 'B90', 'FDP', 'Linke', 'Independent');




    // List of groups = species here = value of the first column called group -> I show them on the X axis
    //const groups = data.map(d => d.group)




    // Add X axis
    const x = d3.scaleBand()
        .domain(fractions)
        .range([0, width])
        .padding([0.2])
    svg.append("g")
        .attr("transform", "translate(" + 0 + "," + height + ")")
        .call(d3.axisBottom(x).tickSizeOuter(0));

    // Add Y axis
    const y = d3.scaleLinear()
        .domain([0, 600])
        .range([height, 0]);
    svg.append("g")
        .call(d3.axisLeft(y));

    x.domain(apliedData.map(function(d) { return d.CxUresults, d.SPDresults, d.AfDresults, d.B90results, d.FDPresults, d.LINKEresults, d.independentresults; }));
    y.domain([0, d3.max(apliedData, function(d) { return d.CxUvalues, d.SPDvalues, d.AfDvalues, d.B90values, d.FDPvalues, d.LINKEvalues, d.independentvalues; })]);


//#####################################################
    // definining subgroups
    // color palette = one color per subgroup
    const color = d3.scaleOrdinal()
        .domain(apliedData)
        .range(['#C7EFCF', '#FE5F55', '#EEF5DB'])

    //stack the data? --> stack per subgroup
    const stackedData = d3.stack()
        .keys(apliedData)
        (apliedData)

    // ----------------
    // Create a tooltip
    // ----------------
    var tooltip = d3.select("#my_dataviz")
        .append("div")
        .style("opacity", 0)
        .attr("class", "tooltip")
        .style("background-color", "white")
        .style("border", "solid")
        .style("border-width", "1px")
        .style("border-radius", "5px")
        .style("padding", "10px")

    // Three function that change the tooltip when user hover / move / leave a cell
    var mouseover = function(d) {
        var subgroupName = d3.select(this.parentNode).datum().key;
        var subgroupValue = d.data[subgroupName];
        tooltip
            .html("subgroup: " + subgroupName + "<br>" + "Value: " + subgroupValue)
            .style("opacity", 1)
    }
    var mousemove = function(d) {
        tooltip
            .style("left", (d3.mouse(this)[0]+90) + "px") // It is important to put the +90: other wise the tooltip is exactly where the point is an it creates a weird effect
            .style("top", (d3.mouse(this)[1]) + "px")
    }
    var mouseleave = function(d) {
        tooltip
            .style("opacity", 0)
    }




    // Show the bars
    svg.append("g")
        .selectAll("g")
        // Enter in the stack data = loop key per key = group per group
        .data(stackedData)
        .enter().append("g")
        .attr("fill", function(d) { return color(d.key); })
        .selectAll("rect")
        // enter a second time = loop subgroup per subgroup to add all rectangles
        .data(function(d) { return d; })
        .enter().append("rect")
        .attr("x", function(d) { return x(d.data.group); })
        .attr("y", function(d) { return y(d[1]); })
        .attr("height", function(d) { return y(d[0]) - y(d[1]); })
        .attr("width",x.bandwidth())
        .attr("stroke", "grey")
        .on("mouseover", mouseover)
        .on("mousemove", mousemove)
        .on("mouseleave", mouseleave)

}


