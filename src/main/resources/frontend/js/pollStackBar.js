function drawStackedBarChart(data, target) {
    console.log(data);



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

    //definiere meine daten, die ich brauche



    // die pollID sind die IDs, der einzelnen Abstimmungen, die auf die x-Achse gehören.



    let pollID = Object.entries(data).map(d =>{
        return {
            pollID: d[1].pollID
        }
    })
    pollID.forEach(function (d) {
        d.pollIDkey = Object.keys(d)[0];
        d.pollIDValue = d[d.pollIDkey]

    });
    var pollIDs = d3.map(pollID, function (d){return(d.pollIDValue)})
    const slicedpollIDs = pollIDs.slice(1, 5);
    console.log(slicedpollIDs);



    /*
    Definition of Subgroups
     */

    // hier sind die Abstimmungen für total und die einzelnen Fraktionen
    let totalData = Object.entries(data).map(d =>{
        return{
            // Total
            totalYes: d[1].totalYes,
            totalAbstained: d[1].totalAbstained,
            totalNo: d[1].totalNo,
            totalNoVotes: d[1].totalNoVotes,
        }
    })

    // Subgroups sind die gestackten Abschnitte
    let subgroups = Object.keys(totalData[1])
    console.log(subgroups);
    let subgroupvotes = Object.values(totalData[1])
    console.log(subgroupvotes);

    // Add X axis
    const x = d3.scaleBand()
        .domain(slicedpollIDs)
        .range([0, width])
        .padding([0.2])
    svg.append("g")
        .attr("transform", "translate(0," +  height + ")")
        .call(d3.axisBottom(x).tickSizeOuter(0));

    // Add Y axis
    const y = d3.scaleLinear()
        .domain([0, 1000])
        .range([height, 0]);

    svg.append("g")
        .call(d3.axisLeft(y));

    // color palette = one color per subgroup
    const color = d3.scaleOrdinal()
        .domain(subgroups)
        .range(['#e41a1c', '#FFC0CB', '#FFFF00', '#4daf4a'])
    //'#4daf4a', '#FFFF00'





    //stack the data? --> stack per subgroup
    const stackedData = d3.stack()
        .keys(subgroups)
        (totalData)
   console.log(stackedData);



    // Show the bars
    svg.append("g")
        .selectAll("g")
        // Enter in the stack data = loop key per key = group per group
        .data(stackedData)
        .join("g")
        .attr("fill", d => color(d.key))
        .selectAll("rect")
        // enter a second time = loop subgroup per subgroup to add all rectangles
        .data(d => d)
        .join("rect")
        .attr("x", d => x(d.data.group))
        .attr("y", d => y(d[1]))
        .attr("height", d => y(d[0]) - y(d[1]))
        .attr("width", x.bandwidth())


}

