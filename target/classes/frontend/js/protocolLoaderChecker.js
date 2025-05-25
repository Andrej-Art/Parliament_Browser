async function loadAll() {
    document.getElementById("load-Status").innerHTML = '<br>Auf Antwort von der DB warten... <img src="/loadIcon.gif" alt="" style="vertical-align: middle"><br>';
    let response = await fetch("/post/applicationDataLoadAll/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({})
    });
    let loadAllAnswer = response.json();
    document.getElementById("load-Status").innerHTML = '';
    if(loadAllAnswer.EditSuccess === "true"){
        window.alert("Laden erfolgreich");
    } else if (loadAllAnswer.EditSuccess === false){
        window.alert("Laden fehlgeschlagen");
    }
    window.location.reload();
}

async function parseAll() {
    document.getElementById("all-Status").innerHTML = '<br>Auf Antwort von der DB warten... <img src="/loadIcon.gif" alt="" style="vertical-align: middle"><br>';
    let response = await fetch("/post/applicationDataParseAll/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({})
    });
    let parseAllAnswer = response.json();
    document.getElementById("all-Status").innerHTML = '';
    if(parseAllAnswer.EditSuccess === true){
        window.alert("Parsen erfolgreich");
    } else if (parseAllAnswer.EditSuccess === false){
        window.alert("Parsen fehlgeschlagen");
    }
    window.location.reload();
}

async function parseNew() {
    document.getElementById("new-Status").innerHTML = '<br>Auf Antwort von der DB warten... <img src="/loadIcon.gif" alt="" style="vertical-align: middle"><br>';
    let response = await fetch("/post/applicationDataParseNew/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({})
    });
    let parseNewAnswer = response.json();
    document.getElementById("new-Status").innerHTML = '';
    if(parseNewAnswer.EditSuccess === true){
        window.alert("Parsen erfolgreich");
    } else if (parseNewAnswer.EditSuccess === false){
        window.alert("Parsen fehlgeschlagen");
    }
    window.location.reload();
}

async function parseSingle() {
    document.getElementById("single-Status").innerHTML = '<br>Auf Antwort von der DB warten... <img src="/loadIcon.gif" alt="" style="vertical-align: middle"><br>';
    let protocolToParse = document.getElementById("protocolToParse").value;
    let response = await fetch("/post/applicationDataParseSingle/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({ protocolToParse: protocolToParse})
    });
    let parseSingleAnswer = response.json();
    document.getElementById("single-Status").innerHTML = '';
    if(parseSingleAnswer.EditSuccess === true){
        window.alert("Parsen erfolgreich");
    } else if (parseSingleAnswer.EditSuccess === false){
        window.alert("Parsen fehlgeschlagen");
    }
    window.location.reload();
}
