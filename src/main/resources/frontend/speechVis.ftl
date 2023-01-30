<!DOCTYPE html>
<html lang="de">
<head>
    <meta name="author" content="Eric Lakhter">
    <title>Rede-Visualisierung</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src="https://d3js.org/d3.v7.min.js"></script>
    <style>
        <#include "css/speechVis.css">
    </style>
</head>
<body>
<h1>Auswahl</h1>
<div> <#-- for now limit this at 160 speeches max for testing convenience-->
<#list 0..speechIDs?size - 1 as i>
    <#if i == 160><#break></#if><button type="button" onclick="getSpeechData('${speechIDs[i]}')">Rede ${speechIDs[i]}</button> <#if ((i + 1) % 8) == 0 ><br></#if>
</#list>
</div>

<h1 id="speechHeader"></h1>

<ul id="speechData"></ul>
<p id="speech"></p>

<script>
/**
 * Accesses speech data on button press and changes the displayed text/information on the web page.
 * @param id The speech ID to <tt>GET</tt> data for.
 * @author Eric Lakhter
 */
function getSpeechData(id = "ID") {
    setPageWaiting();
    let req = new XMLHttpRequest();
    req.open("GET", "/reden/ajax/?speechID=" + id);
    req.responseType = "json";
    req.onload = function () {
        try {
            setPageSpeechVis(req.response);
        } catch (e) {
            console.error(e);
            setPageDefault();
        }
    }
    req.send();
}

/**
 * Sets the page to its default configuration.
 * @author Eric Lakhter
 */
function setPageDefault() {
    document.getElementById("speechHeader").innerHTML = '';
    document.getElementById("speechData").innerHTML = '';
    document.getElementById("speech").innerHTML = '';
}

/**
 * Shows that the query is being processed.
 * @author Eric Lakhter
 */
function setPageWaiting() {
    document.getElementById("speechHeader").innerHTML = '';
    document.getElementById("speechData").innerHTML = '';
    document.getElementById("speech").innerHTML = 'Auf Antwort von DB warten';
}

/**
 * Displays all the relevant speech information on the page.
 * @param speechData JSON containing all of a speech's data.
 * @author Eric Lakhter
 */
function setPageSpeechVis(speechData = {}) {
    let speakerData = speechData["speaker"];
    let perData = speechData["namedEntitiesPer"];
    let orgData = speechData["namedEntitiesOrg"];
    let locData = speechData["namedEntitiesLoc"];
    let sentenceData = speechData["sentences"];
    let commentData = speechData["comments"];
    commentData.sort((a, b) => {return parseInt(a["_id"].split("/")[1]) - parseInt(b["_id"].split("/")[1])});
    console.log(commentData);
    let fullName = speakerData["firstName"] + ' ' + speakerData["lastName"];
    document.getElementById("speechHeader").innerHTML = 'Rede ' + speechData["speechID"] + ' von ' + fullName;
    document.getElementById("speechData").innerHTML =
        '<li>Redner: ' + fullName + ' <img alt="Profilbild" src="' + speakerData["picture"][0] + '" class="speakerPic"></li>' +
        '<li>Partei: ' + speakerData["party"] + '</li>' +
        '<li>Datum: ' + speechData["date"] + '</li>' +
        '<li>Durchschnittliches Sentiment: ' + speechData["speechSentiment"].toFixed(4) + '</li>';
    document.getElementById("speech").innerHTML = applyDataToSpeech(
        speechData["text"],
        perData,
        orgData,
        locData,
        sentenceData,
        commentData
    );
}

<#include "js/speechVis.js">

</script>

</body>
</html>
