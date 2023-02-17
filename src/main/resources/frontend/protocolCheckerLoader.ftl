<!DOCTYPE html>
<html lang="de">
<!DOCTYPE html>
<html>
<!DOCTYPE html>
<html>
<head>
    <!-- source: https://iq.opengenus.org/sha1-algorithm/ -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/js-sha1/0.6.0/sha1.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <meta name="author" content="Julian Ocker">
    <title>Protokoll Lademenü</title>
    <style>
        <#include "css/parliamentBrowser.css">
    </style>
</head>
<br>
<#include "parliamentBrowser.ftl">
<br>
<br>
<br>
<h3>Administration der Protokolle</h3>
<br>
Es sind ${numberOfParsedProtocols} Protokolle in der DB. <br>
Es sind ${numberOfProtocols} zum Parsen verfügbar.<br>
<br>
Warten sie nach dem Drücken eines Knopfes bitte auf die Erfolgs oder Fehlermeldung.
<br>
<br>
<br>

<form onsubmit="loadAll(); return false;">
    Drücken Sie diesen Knopf um alle Protokolle zu laden. <br><br>
    <button type="submit">Alle Protokolle laden</button>
    <div id="load-Status">

    </div>
</form>
<br><br>
<form onsubmit="parseAll(); return false;">
    Drücken Sie diesen Knopf um alle Protokolle neuzuparsen. <br><br>
    <button type="submit">Alle Protokolle parsen</button>
    <div id="all-Status">

    </div>
</form>
<br><br>
<form onsubmit="parseNew(); return false;">
    Drücken Sie diesen Knopf um nur die noch nicht geladenen Protokolle zu laden und neuzuparsen. <br><br>
    <button type="submit">Neue Protokolle parsen</button>
    <div id="new-Status">

    </div>
</form>
<br><br>
<form onsubmit="parseSingle(); return false;">
    Wählen Sie ein Protokoll aus dem Dropdownmenü und drücken sie den Knopf um das spezifische Protokoll parsen zu lassen. <br>
    (Hinweis die Protokolle sind benannt in dem Schema (Wahlperiode * 1000 + (Protokollnummer in der Wahlperiode)) gefolgt von der Zeichenkette "-data.xml" . )<br><br>
    <select id="protocolToParse">
        <#list options as option>
            <option value=${option}>
                ${option.getName()}
            </option>
        </#list>
    </select>
    <button type="submit">Dieses Protokoll parsen</button>
    <div id="single-Status">

    </div>
</form>
<br><br>
<SCRIPT>
    <#include "js/protocolLoaderChecker.js">
</SCRIPT>

</html>
