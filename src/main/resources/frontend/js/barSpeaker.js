/**
 * @author Andrej Artuschenko
 */


function speakerbarchart(data, target) {


    const margin = {top: 30, right: 30, bottom: 70, left: 60},
        width = 460 - margin.left - margin.right,
        height = 400 - margin.top - margin.bottom;

    // Set ranges for the axis
    var x = d3.scaleBand()
        .range([0, width])
        .padding(0.5);

    var y = d3.scaleLinear()
        .range([height, 0])

    // append the svg object to the body of the page
    const svg = d3.select(target)
        .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");


    // sort data


    //d3.csv("https://raw.githubusercontent.com/holtzy/data_to_viz/master/Example_dataset/7_OneCatOneNum_header.csv").then(function (data3) {

    // sort data
    /*
data.sort(function (b, a) {
    return a.Value - b.Value;
});

     */

    // Selecting the right fields from the returned JSON object to match the right values
    let apliedData = Object.entries(data).map(d => {
        return {
            speakerName: d[1].speakerName,
            speechCount: d[1].speechCount,
            picture: d[1].picture
        }
    });



    // sort data by speechcount
    apliedData.sort(function (b, a) {
        return a.speechCount - b.speechCount;
    });





    // extracting the data and setting the key and value
    // set speakerName as key
    apliedData.forEach(function(d) {
        d.key = Object.keys(d)[0];
        d.speakerName = d[d.key]
        d.value = d.speechCount;
        d.speakerPic = d.picture


    });



    // Adjusting the scaling of the axes to fit the dataset scope
    // speakerName on x-Axis and speechCount in y-Axis
    x.domain(apliedData.map(function(d) { return d.speakerName; }));
    y.domain([0, d3.max(apliedData, function(d) { return d.speechCount; })]);

/*

    // Scale the range of the data in the domains. We set the x and y axis here
    // We tell the X axis to render the name and we tell the y axis to render the count
    x.domain(apliedData.map(function (d) {
        return d.speakerName;
    }));

    y.domain([0, d3.max(apliedData, function (d) {
        return d.speechCount;
    })]);

 */



    var tooltip = d3.select("body")
        .append("div")
        .attr("id", "mytooltip")
        .style("position", "absolute")
        .style("z-index", "10")
        .style("visibility", "hidden")
        .text("a simple tooltip");

    // Bars
    svg.selectAll("bar")
        .data(apliedData)
        .enter()
        .append("rect")
        .attr("class", "bar")
        .attr("x", function (d) {
            return x(d.speakerName);
        })
        .attr("y", function (d) {
            return y(d.value);
        })
        .attr("width", x.bandwidth())
        .attr("height", function (d) {
            return height - y(d.value);
        })
        .attr("fill", "#69b3a2")

        // Adding a tooltip functionality to the chart, changing the opacity
        // when the mouse hovers over
        .on("mouseover", function (d,i) {
            d3.select(this)
            tooltip
                .html(

                    '<div>' + i.speakerName + '</div>' + '<div>'+ i.value + " Reden" + '</div>' + '<img class = "speakerpic" alt="Profilbild" src="' + i.speakerPic[0] + '" />'
                )

                .style('visibility', 'visible');
            d3.select(this).transition().attr('fill', "#ffff00");
        })
        .on("mousemove", function (){
            tooltip
                .style('top', d3.event.pageY - 10 + 'px')
                .style('left', d3.event.pageX + 10 + 'px');
        })
        .on("mouseout", function (){
            tooltip.html(``).style('visibility', 'hidden');
            d3.select(this).transition().attr('fill', "#69b3a2");
        });

           // var xPos = parseFloat(d3.select(this).attr("x")) / 2 ;
            //var yPos = parseFloat(d3.select(this).attr("y")) / 2 + height / 2;

            //Update Tooltip
            /*
            d3.select('#tooltip')
                .style('left', xPos + 'px')
                .style('top', yPos + 'px')
                .select('#value').text(d.speakerName)

            d3.select(this)
                .style("opacity", 0.5)
                .style(d.picture);
            svg.append("text")
                .attr("id", "tooltip")
                .attr("x", x(d.speakerName) + x.bandwidth() / 2)
                .attr("y", y(d.speechCount) - 5)
                .text(d.speakerName);

             */
    // And now, after we got our bars, lets append the x and y axis
    // Add x axis

    /*

    svg.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x));

     */

    // Add y axis
    svg.append("g")
        .call(d3.axisLeft(y))



    // Add x axis
    svg.append("g")
        .attr("class", "x")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x))
        .selectAll("text")
        .style("text-anchor", "end")
        .attr("dx", "-.8em")
        .attr("dy", ".15em")
        .attr("transform", "rotate(-20)");


}
