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
<#list 0..speechIDs?size - 1 as i>
    <button type="button" onclick="getSpeechData('${speechIDs[i]}')">Rede ${speechIDs[i]}</button>
    <#if ((i + 1) % 2) == 0 >
        <br>
    </#if>
</#list>

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
        let req = new XMLHttpRequest();
        req.open("GET", "/ajax/reden/?speechID=" + id);
        req.responseType = "json";
        req.onload = function () {
            try {
                setPage(req.response);
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
     * Displays all the relevant speech information on the page.
     * @param allSpeechData JSON array containing all speech data.
     * @author Eric Lakhter
     */
    function setPage(allSpeechData = [{}]) {
        let speechData = allSpeechData[0];
        let speakerData = speechData["speaker"];
        let perData = speechData["namedEntitiesPer"];
        let orgData = speechData["namedEntitiesOrg"];
        let locData = speechData["namedEntitiesLoc"];
        let sentenceData = speechData["sentences"];
        // combine all commentData into one array and then sort it
        let commentData = [];
        for (let i in allSpeechData) {
            commentData.push(allSpeechData[i]["comment"]);
            if (allSpeechData[i]["CommentatorData"] !== undefined) {
                commentData[i]["CommentatorData"] = allSpeechData[i]["CommentatorData"];
            }
        }
        commentData.sort((a, b) => {return parseInt(a["_id"].split("/")[1]) - parseInt(b["_id"].split("/")[1])});
        let fullName = speakerData["firstName"] + ' ' + speakerData["lastName"];
        document.getElementById("speechHeader").innerHTML = 'Rede ' + speechData["speechID"] + ' von ' + fullName;
        document.getElementById("speechData").innerHTML =
            '<li>Redner: ' + fullName + ' <img alt="Profilbild" src="' + speakerData["picture"][0] + '" style="width:60px;height:50px;"></li>' +
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
