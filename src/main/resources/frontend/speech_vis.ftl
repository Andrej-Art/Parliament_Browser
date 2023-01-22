<!DOCTYPE html>
<html lang="de">
<head>
    <meta name="author" content="Eric Lakhter">
    <title>${title}</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src="https://d3js.org/d3.v7.min.js"></script>
</head>

<body>
<h1>🎃</h1>
🍪🍪🍪

<div id="plot1"></div>
<script>
    let speechData = [];
    <#list speechData as speechDatum>
        speechData.push(${speechDatum});
    </#list>

    <#include "js/speech_vis.js">

</script>
</body>

</html>