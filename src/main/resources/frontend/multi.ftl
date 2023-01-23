<html lang="de">

<head>
    <title>Multiple Linechart Test</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <script src = "https://d3js.org/d3.v7.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
</head>

<body>

<h1>Multiline Chart for Named Entities</h1>

<div id="multiline"></div>



</body>

<script>


<#include "js/multilinetest.js">

    $(document).ready(function(){
        const originalData = {
            "2000-01-01": {per: 4, org: 7, loc: 2},
            "2000-01-02": {per: 3, org: 4, loc: 5},
            "2000-01-03": {per: 2, org: 9, loc: 1},
            "2000-01-05": {per: 3, org: 7, loc: 2},
            "2000-01-06": {per: 4, org: 12, loc: 17},
            "2000-01-07": {per: 7, org: 4, loc: 23},
            "2000-01-08": {per: 3, org: 5, loc: 11},
            "2000-01-09": {per: 5, org: 6, loc: 23},
            "2000-01-12": {per: 12, org: 6, loc: 12},
            "2000-01-13": {per: 2, org: 9, loc: 13},
            "2000-01-14": {per: 3, org: 2, loc: 5},
            "2000-01-15": {per: 4, org: 4, loc: 7},
        }
        MultiLineEntities(originalData, '#multiline');

    })
</script>

</html>