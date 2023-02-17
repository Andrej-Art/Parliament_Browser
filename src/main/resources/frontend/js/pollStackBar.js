/**
 * @author Andrej Artuschenko
 * @author Eric Lakhter
 * @param data
 * @param target
 * This function maps the voting results. The d3 Library was used as a reference.
 */

function drawStackedBarChart(data, target) {
    console.log(data);

    if (data.length === 0) {
        console.log("Innerhalb der Filter gibt es keine keine Abstimmungsergebnisse.");
        return;
    }

    // votes for total and the fractions
    let totalData = data.map(d =>{
            switch (document.getElementById("fractionInput").value) {
                case "SPD":
                    return {
                        topic: d.topic,
                        totalNoVotes: d.SPDNoVotes,
                        totalAbstained: d.SPDAbstained,
                        totalNo: d.SPDNo,
                        totalYes: d.SPDYes,
                    }
                case "CDU/CSU":
                    return {
                        topic: d.topic,
                        totalNoVotes: d.CxUNoVotes,
                        totalAbstained: d.CxUAbstained,
                        totalNo: d.CxUNo,
                        totalYes: d.CxUYes,
                    }
                case "FDP":
                    return {
                        topic: d.topic,
                        totalNoVotes: d.FDPNoVotes,
                        totalAbstained: d.FDPAbstained,
                        totalNo: d.FDPNo,
                        totalYes: d.FDPYes,
                    }
                case "BÜNDNIS 90/DIE GRÜNEN":
                    return {
                        topic: d.topic,
                        totalNoVotes: d.B90NoVotes,
                        totalAbstained: d.B90Abstained,
                        totalNo: d.B90No,
                        totalYes: d.B90Yes,
                    }
                case "DIE LINKE.":
                    return {
                        topic: d.topic,
                        totalNoVotes: d.LINKENoVotes,
                        totalAbstained: d.LINKEAbstained,
                        totalNo: d.LINKENo,
                        totalYes: d.LINKEYes,
                    }
                case "AfD":
                    return {
                        topic: d.topic,
                        totalNoVotes: d.AfDNoVotes,
                        totalAbstained: d.AfDAbstained,
                        totalNo: d.AfDNo,
                        totalYes: d.AfDYes,
                    }
                case "fraktionslos":
                    return {
                        topic: d.topic,
                        totalNoVotes: d.independentNoVotes,
                        totalAbstained: d.independentAbstained,
                        totalNo: d.independentNo,
                        totalYes: d.independentYes,
                    }
                default:
                    return {
                        topic: d.topic,
                        totalNoVotes: d.totalNoVotes,
                        totalAbstained: d.totalAbstained,
                        totalNo: d.totalNo,
                        totalYes: d.totalYes,
                    }
            }
    })

    // Subgroups sind die gestackten Abschnitte
    let subgroups = Object.keys(totalData[0]);


    let topics = data.map(d => d.topic);

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


    // Add X axis
    const x = d3.scaleBand()
        .domain(topics)
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
        .range(['#4daf4a', '#FFFF00', '#FFC0CB', '#e41a1c'])
    //'#4daf4a', '#FFFF00'


    //stack the data? --> stack per subgroup
    const stackedData = d3.stack()
        .keys(subgroups)
        (totalData)

    // ----------------
    // Create a tooltip
    // ----------------
    const tooltip = d3.select(target)
        .append("div")
        .style("opacity", 0)
        .attr("class", "tooltip")
        .style("background-color", "white")
        .style("border", "solid")
        .style("border-width", "1px")
        .style("border-radius", "5px")
        .style("padding", "10px")

    // functions that change the tooltip when user hover / move / leave a bar
    const mouseover = function(event, d) {
        const topic = d.data.topic;
        const subgroupName = d3.select(this.parentNode).datum().key;
        const subgroupValue = d.data[subgroupName];

        tooltip
            .html("Topic: " +topic+"<br>" +"Abstimmung: " + subgroupName + "<br>" + "Stimmen: " + subgroupValue)
            .style("opacity", 1)

    }
    const mousemove = function(event, d) {
        tooltip.style("transform","translateY(-55%)")
            .style("left",(event.x)/2+"px")
            .style("top",(event.y)/2-30+"px")
    }
    const mouseleave = function(event, d) {
        tooltip
            .style("opacity", 0)
    }

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
        .attr("x", d => x(d.data.topic))
        .attr("y", d => y(d[1]))
        .attr("height", d => y(d[0]) - y(d[1]))
        .attr("width", x.bandwidth())
        .on("mouseover", mouseover)
        .on("mousemove", mousemove)
        .on("mouseleave", mouseleave)

}

