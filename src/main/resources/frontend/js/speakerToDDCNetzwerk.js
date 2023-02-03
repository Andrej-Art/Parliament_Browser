var w = $('#tags').width(), h = $('#tags').height();
    var fill = d3.scale.category10();

    var vis = d3.select('#tags').append("svg:g");

    d3.json("${networkData}", function(json) {

        var edges = [];
        json.Links.forEach(function(e) {
            var sourceNode = json.Nodes.filter(function(n) {
                return n.name === e.Source;
            })[0],
                targetNode = json.Nodes.filter(function(n) {
                    return n.name === e.Target;
                })[0];

            edges.push({
                source: sourceNode,
                target: targetNode,
                value: e.Value
            });
        });


      var force = d3.layout.force()
        .charge(-3000)
        .distance(200)
        .gravity(0.2)
        .nodes(json.nodes)
        .links(edges)
        .size([700, 1500])
        .start();

      var link = vis.selectAll('line.link')
          .data(edges)
        .enter().append('svg:line')
          .attr("class", "link")
		  .attr("marker-end", "url(#arrow)")
		.attr("stroke", "red")
          .style('stroke-width', 2)
          .attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; });

      var node = vis.selectAll("circle.node")
          .data(json.nodes)
        .enter().append("svg:circle")
          .attr("class", "node")
		  .attr("name", function(d) { return d.name; })
          .attr("cx", function(d) { return d.x; })
          .attr("cy", function(d) { return d.y; })
          .attr("r", 40)
          .style("fill", function(d) { return fill(d.color); })
		  .on('dblclick', function(d) {
					alert(d.name);}
				)
          .call(force.drag);

		var text = vis.selectAll("text")
			.data(json.nodes)
			.enter().append("svg:text")
			.attr("x", function(d) { return d.x ; })
			.attr("y", function(d) { return d.y -10; })
			.attr("stroke", function(d) { return fill(d.color); })
			.text(function(d) { return d.name; })
			.attr("font-size","40")
			.call(force.drag);

      node.append('title')
          .text(function(d) { return d.name; });




      force.on("tick", function(e) {
        node.each(function(d) {
            d.x += ((5-d.group) * 350 - d.x) ;
			d.x = 1500 - d.x;
        });

		text.each(function(d) {
            d.x += ((5-d.group) * 350 - d.x) ;
			d.x = 1500 - d.x;
        });

        link.attr("x1", function(d) { return d.source.x; })
            .attr("y1", function(d) { return d.source.y; })
            .attr("x2", function(d) { return d.target.x; })
            .attr("y2", function(d) { return d.target.y; });

        node.attr("cx", function(d) { return d.x ; })
            .attr("cy", function(d) { return d.y; });

		text.attr("x", function(d) { return d.x ; })
            .attr("y", function(d) { return d.y; });
      });

    });