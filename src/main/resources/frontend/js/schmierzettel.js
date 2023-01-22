// Schmierzettel für JSON in javascript

const son = JSON.parse('{"perEntity":[{"_id":2,"name":"butter"},{"_id":5,"name":"butter"}],' +
    '"locEntity":[{"_id":3,"name":"butter"},{"_id":1,"name":"butter"}],' +
    '"orgEntity":[{"_id":10,"name":"butter"},{"_id":500,"name":"butter"}]}');
console.log(son);
// for (let i = 0; i < son["perEntity"].length; i++) {
//     console.log(son["perEntity"][i]["_id"]);
// }
for (let obj in son) {
    for (let i in son[obj]) {
        console.log("Für das Element \"" + obj + "\" hat Array-Element", i, "den Eintrag", son[obj][i]["_id"]);
    }
}
let per_data_set = son["perEntity"];
let org_data_set = son["orgEntity"];
let loc_data_set = son["locEntity"];
console.log(per_data_set);
console.log(org_data_set);
console.log(loc_data_set);


