<!DOCTYPE html>
<html lang="de">
<head>
    <meta name="author" content="Eric Lakhter">
    <title>Rede-Visualisierung</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src="https://d3js.org/d3.v7.min.js"></script>
</head>
<body>
<h1>🎃</h1>
🍪🍪🍪
<p id="speech">
<ul>
    <#list 1..speechData?size as index>
        <li>${index - 1}</li>
    </#list>
</ul>
</p>

<div id="plot1"></div>
<script>
    <#include "js/speech_vis.js">

    let speech_data = ${speechData[0]};
    let commentatorData = [];
    // combine all commentData into one array
    <#list 0..speechData?size - 1 as index>
        commentatorData.push(${speechData[index]}["commentatorData"]);
        commentatorData[${index}]["commentsPos"] = ${speechData[0]}["commentsPos"][${index}];
    </#list>
    console.log(commentatorData);

    console.log(speech_data);
    console.log("butter");
    let speechTag = document.getElementById("speech");
    // speechTag.innerText = "butter";
</script>
</body>

</html>