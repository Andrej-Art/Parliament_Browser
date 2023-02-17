/**
 * Function to generate a Multi_Line_Chart for the Named Entities
 * @param data the query data
 * @param target the   target html element
 * @author DavidJordan
 * // Sources: https://observablehq.com/@d3/multi-line-chart
 * // Sources: https://d3-graph-gallery.com/graph/line_several_group.html
 * // This function, like the other d3.js chart-functions I contributed to, was pieced together from
 * // various foreign code examples. My own original contribution was in adapting the code
 * // to fit our unique dataset and adding comments.
 */
function MultiLineEntities(data, target){

    let sortedData = {};
    Object.keys(data).sort().forEach(function(key) {
        sortedData[key] = data[key];
    });
 let originalData = sortedData;

    // Setting the margins to the same dimensions as all other charts
    let margin = {top: 10, right: 30, bottom: 60, left: 40},
        width = 460 - margin.left - margin.right,
        height = 400 - margin.top - margin.bottom;

    // Parsing the date
    let parseDate = d3.timeParse("%Y-%m-%d");

    // Scaling the  x and y axis
    let x = d3.scaleTime()
        .range([0, width]);
    let y = d3.scaleLinear()
        .range([height, 0]);

    // Setting the colors of the respective fields
    let color = d3.scaleOrdinal()
        .domain(["personEntity", "orgEntity", "locationEntity"])
        .range(["red", "blue", "green"]);

    // Setting the axis descriptions
    let xAxis = d3.axisBottom(x)
        .tickFormat(d3.timeFormat("%Y-%m-%d"));
    let yAxis = d3.axisLeft(y);

    // Mapping the values to the line
    let line = d3.line()
        .x(function(d) { return x(d.date); })
        .y(function(d) { return y(d.value); });

    // Creating the svg object
    let svg = d3.select(target).append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    // Selecting the right fields from the returned JSON object to match the right values
    let processedData = Object.entries(originalData).map(d => {
        return {
            date: parseDate(d[0]),
            personEntity: d[1].personEntity,
            orgEntity: d[1].orgEntity,
            locationEntity: d[1].locationEntity,
        }
    });

    // setting the color values
    color.domain(Object.keys(processedData[0]).filter(function(key) { return key !== "date"; }));
    let types = color.domain().map(function(name) {
        return {
            name: name,
            values: processedData.map(function(d) {
                return {date: d.date, value: +d[name]};
            })
        };
    });


    // Setting the x domain to the date and the y domain to the respective values
    x.domain(d3.extent(processedData, function(d) { return d.date; }));
    y.domain([
        d3.min(types, function(c) { return d3.min(c.values, function(v) { return v.value; }); }),
        d3.max(types, function(c) { return d3.max(c.values, function(v) { return v.value; }); })
    ]);


    // Building the svg element , setting x-axis rotation so that the date values are legible
    svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis)
        .selectAll("text")
        .style("text-anchor", "end")
        .attr("dx", "-.8em")
        .attr("dy", ".15em")
        .attr("transform", "rotate(-20)");



    // Building the svg element , setting y-axis
    svg.append("g")
        .attr("class", "y axis")
        .call(yAxis)
        .append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 6)
        .attr("dy", ".71em")
        .style("text-anchor", "end")
        .text("Values");


    // Matching each of the three different types to the accordingly colored line
    types.forEach(function(type) {
        // Creating the lines and setting the corresponding color to each
        svg.append("path")
            .attr("class", "line")
            .attr("d", line(type.values))
            .style("fill", "none")
            .style("stroke", color(type.name))
            .style("stroke-width", 2);

        // Creating a circle to denote each datapoint
        svg.selectAll("dot")
            .data(processedData)
            .enter().append("circle")
            .attr("r", 5)
            .attr("cx", function(d) { return x(d.date); })
            .attr("cy", function(d) { return y(d[type.name]); })
            .style("fill", color(type.name));

        // I also wanted to add a tooltip with a hover effect, but it did not work
        // regardless of what I tried. So no hover effect for this chart
    });


    // Adding a legend to the chart
    let legend = svg.selectAll(".legend")
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


/**
 *  A function to create a Bar chart
 * @param data The data to be mapped to the chart
 * @param target the target html element
 * @author DavidJordan
 * // Sources: https://observablehq.com/@d3/bar-chart?collection=@d3/charts
 * // Sources: https://d3-graph-gallery.com/barplot.html
 * // This function like the other d3.js chart-functions I contributed to was pieced together from
 * // various foreign code examples. My own original contribution was in adapting the code
 * // to fit our unique dataset and adding comments.
 */
function createBarChart(data, target) {
    // Setting the margins to the same dimensions as all other charts
    const margin = {top: 10, right: 30, bottom: 60, left: 80},
        width = 460 - margin.left - margin.right,
        height = 400 - margin.top - margin.bottom;

    // setting the x and y scales
    const x = d3.scaleBand()
        .range([0, width])
        .padding(0.1);
    const y = d3.scaleLinear()
        .range([height, 0]);

    // selecting the target html element to insert into and matching it to the margins
    const svg = d3.select(target).append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

    // extracting the data and setting the key and value
    data.forEach(function(d) {
        d.key = Object.keys(d)[0];
        d.value = d[d.key];
    });

    // Adjusting the scaling of the axes to fit the dataset scope
    x.domain(data.map(function(d) { return d.key; }));
    y.domain([0, d3.max(data, function(d) { return d.value; })]);

    // appending  the rectangles for the bar chart
    svg.selectAll(".bar")
        .data(data)
        .enter().append("rect")
        .attr("class", "bar")
        .attr("x", function(d) { return x(d.key); })
        .attr("width", x.bandwidth())
        .attr("y", function(d) { return y(d.value); })
        .attr("height", function(d) { return height - y(d.value); })
        // Adding a tooltip functionality to the chart, changing th opacity
        // when the mouse hovers over
        .on("mouseover", function(d) {
            d3.select(this)
                .style("opacity", 0.5);
            //testing
            svg.append("text")
                .attr("id", "tooltip")
                .attr("x", width - margin.right)
                .attr("y", margin.top);
        })
        .on("mouseout", function(d) {
            d3.select(this)
                .style("opacity", 1);
            d3.select("#tooltip").remove();
        });

    // add the x Axis
    svg.append("g")
        .attr("transform", "translate(0," + height + ")");
       // .call(d3.axisBottom(x));

    // add the y Axis
    svg.append("g")
        .call(d3.axisLeft(y));

    var xAxis = svg.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x))
        .selectAll("text")
        .style("text-anchor", "end")
        .attr("dx", "-.8em")
        .attr("dy", ".15em")
        .attr("transform", "rotate(-35)");
}


/**
 * Function to create a Linechart and insert it into the target html element.
 * @param data the data delivered from the query
 * @param target the target html element
 * @author DavidJordan
 * // Sources: https://observablehq.com/@d3/line-chart?collection=@d3/charts
 * // Sources: https://d3-graph-gallery.com/graph/line_basic.html
 * // This function like the other d3.js chart-functions I contributed to was pieced together from
 * // various foreign code examples. My own original contribution was only in adapting the code
 * // to fit our unique dataset and adding comments.
 */
function createLineChart(data, target) {
    // Setting the margins and dimensions, adjust margins to allow for large numbers
    var margin = {top: 10, right: 30, bottom: 70, left: 80},
        width = 460 - margin.left - margin.right,
        height = 400 - margin.top - margin.bottom;

    // scaling the x and y axis
    var x = d3.scaleBand()
        .range([0, width])
        .padding(0.1);
    //testing
    var y = d3.scaleLinear()
        .range([height, 0])
        .nice();

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
    //testing
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


    // More axis parameters
    svg.append("g")
        .attr("transform", "translate(0," + height + ")");
    svg.append("g")
        .call(d3.axisLeft(y));

    //creating a tooltip feature  that displays the label and value pair when hovering over a blue circle
    var tooltip = svg.append("text")
        .attr("id", "tooltip")
        .style("display", "none");

    // Adding the blue circle feature to the svg denoting each datapoint
    svg.selectAll("circle")
        .data(data)
        .enter()
        .append("circle")
        .attr("cx", function(d) { return x(d.label) + x.bandwidth()/2; })
        .attr("cy", function(d) { return y(d.value); })
        .attr("r", 5)
        .style("fill", "steelblue")
        .attr("title", function(d) { return d.label;})
        // Creating the hover effect for the circles causing a change in opacity when mouse hovers over it
        .on("mouseover", function(d) {
            d3.select(this)
                .style("opacity", 0.5);
            tooltip
                .style("right", d3.event.pageX + "px")
                .style("top", d3.event.pageY + "px")
                .style("display", "block")
                .text(d.label + ": " + d.value);
        })
        // the corresponding change when the mouse leaves the circle
        .on("mouseout", function(d) {
            d3.select(this)
                .style("opacity", 1);
            tooltip
                .style("display", "none");
        });

    // Making sure to rotate the labels on the x-axis for better readabillity
    var xAxis = svg.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x))
        .selectAll("text")
        .style("text-anchor", "end")
        .attr("dx", "-.8em")
        .attr("dy", ".15em")
        .attr("transform", "rotate(-55)");

}



/**
 * Function to update the charts with data filtered by the user entry on the website.
 * @author DavidJordan
 * @author Andrej Artuschenko
 */
function updateCharts() {
    // The date filters from the calendar fields
    const startDate = document.getElementById("von").value;
    const endDate = document.getElementById("bis").value;
    // The person filter from the search field selecting the personFilter
    const person = document.getElementById("personInput").value;
    //The fraction and party filters
    const fraction = document.getElementById("fractionInput").value;
    const party = document.getElementById("partyInput").value;
    $('#status-message-box').text('Waiting for response from DB ...');

    // Make an AJAX call to the backend
    var ajaxChartData = new XMLHttpRequest();
    ajaxChartData.open("GET", "/update-charts/?von=" + startDate + "&bis=" + endDate + "&personInput=" + person + "&fraction=" + fraction + "&party=" + party, true);
    ajaxChartData.responseType = "json";
    ajaxChartData.onreadystatechange = function() {
        $('#status-message-box').text('');
        // if successful
        if (ajaxChartData.readyState === XMLHttpRequest.DONE && ajaxChartData.status === 200) {

            // Add the new chart to the div element of the acrual panel and card

            var chartDivIdArray = ["pos", "tokenLine", "spider", "entitiesMulti", "my_dataviz", "pie"];
            for(var i=0; i< chartDivIdArray.length; i++) {
                let chart1Container = document.getElementById(chartDivIdArray[i]);


                // Clears all the old charts out of the div elements
                var chartDivIdArray = ["pos", "tokenLine", "spider", "entitiesMulti", "my_dataviz", "pie"];
                for (var i = 0; i < chartDivIdArray.length; i++) {
                    let chart1Container = document.getElementById(chartDivIdArray[i]);
                    //das muss für das neue Panel ganz entfernt werden, da charts nicht mehr gelöscht werden an der gleichen stelle,
                    // sondern neue Panels erstellt werden
                    /*

                    while (chart1Container.firstChild) {
                        chart1Container.removeChild(chart1Container.firstChild);
                    }

                     */


                }
            }

            let data = ajaxChartData.response

            let entityData = data["entities"];
            let posdata = data["pos"];
            let tokenData = data["token"];
            let speechData = data["speechesNumber"];
            let sentimentData = data["sentiment"];
            let voteData = data["votes"];
           // console.log(speechData);
            //console.log(sentimentData);
            //console.log(voteData);


            MultiLineEntities(entityData, "#entitiesMulti"+(panelCount));
            createLineChart(tokenData,"#tokenLine"+(panelCount));
            createBarChart(posdata, "#pos"+(panelCount));
            //drawSpiderChart(sentimentData,"#spider"+(panelCount));
            drawStackedBarChart(voteData,"#pie"+(panelCount));
          speakerbarchart(speechData,"#my_dataviz"+(panelCount));



            // Create and insert new charts

            /*



           MultiLineEntities(entityData, document.querySelector("#entitiesMulti"));
            createLineChart(tokenData, document.querySelector("#tokenLine"));
            createBarChart(posdata, document.querySelector("#pos"));
            drawSpiderChart(sentimentData, document.querySelector("#spider"));
            drawStackedBarChart(voteData,document.querySelector("#pie"));
            speakerbarchart(speechData,document.querySelector("#my_dataviz"));

             */





        } else {
            console.log("Error: " + ajaxChartData.status);
        }
    };
    ajaxChartData.send();
}







