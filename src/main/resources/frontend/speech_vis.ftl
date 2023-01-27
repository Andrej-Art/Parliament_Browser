<!DOCTYPE html>
<html lang="de">
<head>
    <meta name="author" content="Eric Lakhter">
    <title>Rede-Visualisierung</title>
<#--    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">-->
    <script src="https://d3js.org/d3.v7.min.js"></script>
    <style>
        .sentiment .hoverText {
            visibility: hidden;
            position: absolute;
            z-index: 1;
            background-color: black;
            text-align: center;
            padding: 5px 5px;
            border-radius: 6px;
        }
        .sentiment:hover .hoverText {
            visibility: visible;
        }
        .sentiment .hoverText::after {
            content: " ";
            position: absolute;
            top: 50%;
            right: 100%;
            margin-top: -5px;
            border-width: 5px;
            border-style: solid;
            border-color: transparent black transparent transparent;
        }
    </style>
</head>
<body>

<h1 id="speechHeader"></h1>

<ul id="speechData"></ul>
<p id="speech"></p>
<p id="test"></p>

<#-- checking out field names -->

<#--    ${speechData[0]}-->

<div id="plot1"></div>
<script>
    <#include "js/speech_vis.js">

    let speechData = ${speechData[0]};
    let speakerData = speechData["speaker"];
    const perData = speechData["namedEntitiesPer"];
    const orgData = speechData["namedEntitiesOrg"];
    const locData = speechData["namedEntitiesLoc"];
    const sentenceData = speechData["sentences"];
    let commentData = [];
    // combine all commentData into one array and then sort it
    <#list 0..speechData?size - 1 as i>
    commentData.push(${speechData[i]}["comment"]);
     if (${speechData[i]}["CommentatorData"] !== undefined) {
         commentData[${i}]["CommentatorData"] = ${speechData[i]}["CommentatorData"];
     }
    </#list>
    commentData.sort((a, b) => {return parseInt(a["_id"].split("/")[1]) - parseInt(b["_id"].split("/")[1])});
    let fullName = speakerData["firstName"] + ' ' + speakerData["lastName"];
    document.getElementById("speechHeader").innerHTML = 'Rede ' + speechData["speechID"] + ' von ' + fullName;
    document.getElementById("speechData").innerHTML =
        '<li>Redner: <img alt="Profilbild" src="' + speakerData["picture"][0] + '" style="width:60px;height:50px;"> ' + fullName + '</li>' +
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