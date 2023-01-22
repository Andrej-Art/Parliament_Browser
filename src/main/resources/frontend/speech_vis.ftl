<!DOCTYPE html>
<html lang="de">
<head>
    <meta name="author" content="Eric Lakhter">
    <title>Rede-Visualisierung</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src="https://d3js.org/d3.v7.min.js"></script>
</head>
<body>
<h1>This page is lit fam 🔥</h1>

🍪☀🍪

<ul>
    <#list 1..speechData?size as index>
        <li><span style="background-color: aqua">${index - 1}</span></li>
    </#list>
</ul>
<p id="speech">
    <span style="background-color: aqua">Das</span> hier ist ein Text.
</p>

<div id="plot1"></div>
<script> // all of this works so far, except for the functions which don't have functionality yet
    <#include "js/speech_vis.js">

    let speechData = ${speechData[0]};

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

    // console.log(sentenceData);
    // console.log(perData);
    // console.log(orgData);
    // console.log(locData);
    // console.log(commentData);

    let speechTag = document.getElementById("speech");
    // speechTag.innerHTML = '<span style="background-color: aqua">Das hier <span style="background-color: blue">ist</span> ein Text.</span>';
    console.log(speechData["text"]);
    speechTag.innerHTML = applyDataToSpeech(
        speechData["text"],
        sentenceData,
        perData,
        orgData,
        locData,
        commentData
    );
    // speechTag.innerHTML = applyDataToSpeech(
    //     speechData["text"],
    //     sentenceData,
    //     perData,
    //     orgData,
    //     locData,
    //     commentData
    // );
</script>
</body>

</html>