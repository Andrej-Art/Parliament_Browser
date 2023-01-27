// Execute this when the document has been loaded
/**
 * @author Andrej Artuschenko
 */
$(document).ready(function () {
    console.log("Im up and running!");


    // Template for any AJAX request. Just change the route below.
    $.ajax({
        url: "http://localhost:4567/dashboard/",
        type: 'GET',
        success: function (data, status) {
            if (status) {
                console.log("Dieser Aufruf war erfolgreich");
            }
            console.log(data);
        },
        error: function (ex) {
            alert("Sorry user, this request didnt work.");
        }
    });
})


var data = [
    {
        className: 'germany', // optional can be used for styling
        axes: [
            {axis: "strength", value: 6},
            {axis: "intelligence", value: 8},
            {axis: "charisma", value: 11},
            {axis: "dexterity", value: 9},
            {axis: "luck", value: 6}
        ]
    },
    {
        className: 'argentina',
        axes: [
            {axis: "strength", value: 7},
            {axis: "intelligence", value: 5},
            {axis: "charisma", value: 7},
            {axis: "dexterity", value: 5},
            {axis: "luck", value: 9}
        ]
    }
];

function drawRadarChart() {

    var chart = RadarChart.chart()
    var svg = d3.select('#body').append('svg')
        .attr('width', 600)
        .attr('height', 800);

// draw one
    svg.append('g').classed('focus', 1).datum(data).call(chart);

// draw many radars
    var game = svg.selectAll('g.game').data(
        [
            data,
            data,
            data,
            data
        ]
    );
    game.enter().append('g').classed('game', 1);
    game
        .attr('transform', function (d, i) {
            return 'translate(150,600)';
        })
        .call(chart);
}


