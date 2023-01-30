// Contains functions which change the displayed options on the site.

/**
 * Replaces the "button-list" &lt;ul> with all protocols.
 * @author Eric Lakhter
 */
function setProtocolButtons() {
    let finalHTML = '';
    for (let protocolID of protocolKeys) {
        finalHTML += '<li><button type="button" onclick="setAgendaButtons(\''
            + protocolID + '\')" class="speech-vis-sidebar-buttons"> Protokoll ' + protocolID + '</button>';
    }
    document.getElementById("button-list").innerHTML = finalHTML;
}

/**
 * Replaces the "button-list" &lt;ul> with all agenda points of that protocol.
 * @param protocolID protocol ID by which to find the agenda points.
 * @author Eric Lakhter
 */
function setAgendaButtons(protocolID = "1/1") {
    let finalHTML = '';
    for (let agendaID of protocols[protocolID]) {
        finalHTML += '<li><button type="button" onclick="setSpeechButtons(\''
            + agendaID + '\')" class="speech-vis-sidebar-buttons">' + agendaID.split("/")[2] + '</button>';
    }
    document.getElementById("button-list").innerHTML = finalHTML;
}

/**
 * Replaces the "button-list" &lt;ul> with all speeches held during the given agenda point
 * @param agendaID agenda point ID by which to find the speeches.
 * @author Eric Lakhter
 */
function setSpeechButtons(agendaID = "1/1/ID") {
    let finalHTML = '';
    for (let speechID of agendaItems[agendaID]["speechIDs"]) {
        finalHTML += '<li><button type="button" onclick="getSpeechData(\''
            + speechID + '\')" class="speech-vis-sidebar-buttons">' + speechID + '</button>';
    }
    document.getElementById("button-list").innerHTML = finalHTML;
}

/**
 * Accesses speech data on button press and changes the displayed speech on the web page.
 * @param id The speech ID to <tt>GET</tt> data for.
 * @author Eric Lakhter
 */
function getSpeechData(id = "ID") {
    setPageWaiting();
    let req = new XMLHttpRequest();
    req.open("GET", "/reden/ajax/?speechID=" + id);
    req.responseType = "json";
    req.onload = function () {
        try {
            setPageSpeechVis(req.response);
        } catch (e) {
            console.error(e);
            setPageDefault();
        }
    }
    req.send();
}

/**
 * Shows that the query is being processed.
 * @author Eric Lakhter
 */
function setPageWaiting() {
    document.getElementById("speechHeader").innerHTML = '';
    document.getElementById("speechData").innerHTML = '';
    document.getElementById("speech").innerHTML = 'Auf Antwort von DB warten';
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
 * @param speechData JSON containing all of a speech's data.
 * @author Eric Lakhter
 */
function setPageSpeechVis(speechData = {}) {
    let speakerData = speechData["speaker"];
    let perData = speechData["namedEntitiesPer"];
    let orgData = speechData["namedEntitiesOrg"];
    let locData = speechData["namedEntitiesLoc"];
    let sentenceData = speechData["sentences"];
    let commentData = speechData["commentData"];
    commentData.sort((a, b) => {return parseInt(a["id"].split("/")[1]) - parseInt(b["id"].split("/")[1])});
    let fullName = speakerData["firstName"] + ' ' + speakerData["lastName"];
    document.getElementById("speechHeader").innerHTML = 'Rede ' + speechData["speechID"] + ' von ' + fullName;
    document.getElementById("speechData").innerHTML =
        '<li>Redner: ' + fullName + ' <img alt="Profilbild" src="' + speakerData["picture"][0] + '" class="speaker-pic"></li>' +
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
