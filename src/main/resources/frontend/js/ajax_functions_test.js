
function loadMultiLineEnities(target){

    $.ajax({
        url: BASIC_URL, //  This URL could then be added to, to fit the correct route
        type: 'GET',
        async: true,
        success: function(a){
            MultiLineEntities(a, target);
        },
        failure: function(error) {
            console.log(error);
        }

    });
}

/*
Javascript Teil
 */

// Hier holen wir die Daten aus den html Elementen im Dashboard
const filters = {
    key1: document.getElementById("#datum1").value,
    key2: document.getElementById("#datum2").value,
    key3: document.getElementById("#fraction").value
};

// Wir nutzen fetch um eine get request mit den Filtern zu senden
fetch("/chart-data?filters=" + JSON.stringify(filters))
    .then(response => response.json())
    .then(data => {
        ourChart(data);
    });

/*
Java Spark Teil

get("/chart-data", (req, res) -> {
    String filtersJson = req.queryParams("filters");
    ObjectMapper mapper = new ObjectMapper();
    Map<String,Object> filters = mapper.readValue(filtersJson, Map.class);

*/