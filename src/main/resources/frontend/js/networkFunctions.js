const partyCode = {cxu: 1, spd: 2, fdp: 3, gruene: 4, afd: 6, linke: 5, parteilos: 7};

/**
 * filters links and nodes by their party if checkbox is unchecked
 * @param party
 * @author Edvin Nise
 */
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

/**
 * hides the links if the respective checkbox is unchecked
 * @param checkbox
 * @author Edvin Nise
 */
function sentimentCheckbox(checkbox) {
    let visibility = checkbox.checked ? "visible" : "hidden";
    link.filter(function (d) {
        switch (checkbox.id) {
            case "pos": return d.sentiment > 0;
            case "neg": return d.sentiment < 0;
            case "neu": return d.sentiment === 0;
            default: throw Error("Was zum Teufel");
        }
    }).style("visibility", visibility);
}
