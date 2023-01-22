<html lang="de">

<head>
    <title>${title}</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src = "https://d3js.org/d3.v7.min.js"></script>
</head>

<body>
<h1>🎃</h1>

<div id="plot1"></div>



<script>
    let data = [];
    <#list objList as obj2>
        data.push(${obj2});
    </#list>



    window.alert(data)
   <#include "js/schmierzettel.js">
    <#include "js/balkentest.js">


    window.alert(per_data_set);
</script>
</body>

</html>