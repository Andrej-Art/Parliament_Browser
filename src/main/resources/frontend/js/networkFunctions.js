const partyCode = {cxu: 1, spd: 2, fdp: 3, gruene: 4, afd: 6, linke: 5, parteilos: 7};

function partyCheckbox(party) {
    let checkbox = document.getElementById(party);
    let visibility = checkbox.checked ? "visible" : "hidden";
    node.filter(function (d) {
        return d.group === partyCode[party];
    }).style("visibility", visibility);
    label.filter(function (d) {
        return d.group === partyCode[party];
    }).style("visibility", visibility);
    link.filter(function (d) {
        return d.source.group === partyCode[party];
    }).style("visibility", visibility);
}


function posSentimentCheckbox() {
    let checkbox = document.getElementById('pos');
    let visibility = checkbox.checked ? "visible" : "hidden";
    link.filter(function (d) {
        return d.sentiment > 0;
    }).style("visibility", visibility);
}

function negSentimentCheckbox() {
    let checkbox = document.getElementById('neg');
    let visibility = checkbox.checked ? "visible" : "hidden";
    link.filter(function (d) {
        return d.sentiment < 0;
    }).style("visibility", visibility);
}

function neuSentimentCheckbox() {
    let checkbox = document.getElementById('neu')
    let visibility = checkbox.checked ? "visible" : "hidden";
    link.filter(function (d) {
        return d.sentiment === 0;
    }).style("visibility", visibility);
}
