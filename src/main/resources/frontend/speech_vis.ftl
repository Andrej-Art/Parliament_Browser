<!DOCTYPE html>
<html lang="de">
<head>
    <meta name="author" content="Eric Lakhter">
    <title>Rede-Visualisierung</title>
<#--    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">-->
    <script src="https://d3js.org/d3.v7.min.js"></script>
</head>
<body>
<h1 id="speechHeader"></h1>

<ul id="speechData"></ul>
<p id="speech"></p>
<p id="test"></p>

<#-- checking out field names -->

    ${speechData[0]}

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
    // combine all commentData into one array and then sort it
    <#list 0..speechData?size - 1 as i>
    commentData.push(${speechData[i]}["comment"]);
     if (${speechData[i]}["CommentatorData"] !== undefined) {
         commentData[${i}]["CommentatorData"] = ${speechData[i]}["CommentatorData"];
     }
    </#list>
    commentData.sort((a, b) => {return a["commentPos"] - b["commentPos"]});

    let fullName = speakerData["firstName"] + ' ' + speakerData["lastName"];
    document.getElementById("speechHeader").innerHTML = 'Rede ' + speechData["speechID"] + ' von ' + fullName;
    document.getElementById("speechData").innerHTML =
        '<li>Redner: ' + fullName + '</li>' +
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
</script>
</body>

</html>