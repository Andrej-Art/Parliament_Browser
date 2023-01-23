<!DOCTYPE html>
<html lang="de">
<head>
    <meta name="author" content="Eric Lakhter">
    <title>Rede-Visualisierung</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src="https://d3js.org/d3.v7.min.js"></script>
</head>
<body>
<h1 id="speechHeader"></h1>

<ul id="speechData"></ul>
<p id="speech"></p>

<div id="plot1"></div>
<script>
    <#include "js/speech_vis.js">

    let speechData = ${speechData[0]};
    let speakerData = speechData["speaker"];
    const sentenceData = speechData["sentences"];
    const perData = speechData["namedEntitiesPer"];
    const orgData = speechData["namedEntitiesOrg"];
    const locData = speechData["namedEntitiesLoc"];
    let commentData = [];
    // combine all commentData into one array
    <#list 0..speechData?size - 1 as i>
    commentData.push(${speechData[i]}["commentatorData"]);
    commentData[${i}]["commentPos"] = speechData["commentsPos"][${i}];
    </#list>

    document.getElementById("speechHeader").innerHTML = 'Rede ' + speechData["speechID"] + ' von ' + speakerData["full_name"];
    document.getElementById("speechData").innerHTML =
        '<li>Redner: ' + speakerData["full_name"] + '</li>' +
        '<li>Partei: ' + speakerData["party"] + '</li>' +
        '<li>Datum: ' + speechData["date"] + '</li>' +
        '<li>Durchschnittliches Sentiment: ' + speechData["speechSentiment"].toFixed(4) + '</li>';
    document.getElementById("speech").innerHTML = applyDataToSpeech(
        speechData["text"],
        sentenceData,
        perData,
        orgData,
        locData,
        commentData
    );
</script>
</body>

</html>