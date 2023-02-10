// Contains functions which change the displayed options on the site.

/**
 * Replaces the "button-list" list with all protocols.
 * @author Eric Lakhter
 */
function setProtocolButtons() {
    let finalHTML = '';
    for (let protocolID of protocolKeys) {
        finalHTML += '<li><button type="button" onclick="setAgendaButtons(\''
            + protocolID + '\')" class="speech-vis-sidebar-button"> Protokoll ' + protocolID + '</button>';
    }
    document.getElementById("button-list").innerHTML = finalHTML;
}

/**
 * Replaces the "button-list" list with all agenda points of that protocol.
 * @param protocolID protocol ID by which to find the agenda points.
 * @author Eric Lakhter
 */
function setAgendaButtons(protocolID = "1/1") {
    let finalHTML = '';
    for (let agendaID of protocols[protocolID]) {
        finalHTML += '<li><button type="button" onclick="setSpeechButtons(\''
            + agendaID + '\')" class="speech-vis-sidebar-button agenda-button">' + agendaID.split("/")[2]+ '</button><br>'
            + agendaItems[agendaID]["subject"].replaceAll('\n', '<br>');
    }
    document.getElementById("button-list").innerHTML = finalHTML;
}

/**
 * Replaces the "button-list" list with all speeches held during the given agenda point
 * @param agendaID agenda point ID by which to find the speeches.
 * @author Eric Lakhter
 */
function setSpeechButtons(agendaID = "1/1/ID") {
    let finalHTML = '';
    for (let speechID of agendaItems[agendaID]["speechIDs"]) {
        finalHTML += '<li><button type="button" onclick="getSpeechData(\''
            + speechID + '\')" class="speech-vis-sidebar-button">' + speechID + '</button>';
    }
    document.getElementById("button-list").innerHTML = finalHTML;
}

/**
 * Finds all speech IDs which contain the queried string, found in the "text-search" input field.
 * @author Eric Lakhter
 */
function findSpeechIDs() {
    let text = document.getElementById("text-search").value;
    let req = new XMLHttpRequest();
    req.open("GET", "/reden/speechIDs/?text=" + text);
    req.responseType = "json";
    req.onload = function () {
        try {
            let finalHTML = '';
            let idArray = req.response["speechIDs"];
            idArray.sort((a, b) => {
                if (a.substring(2, 4) === b.substring(2, 4)) {
                    return parseInt(a.substring(4)) - parseInt(b.substring(4));
                }
                if (a.substring(2, 4) < b.substring(2, 4)) {
                    return -1;
                }
                return 1;
            });
            for (let speechID of idArray) {
                finalHTML += '<li><button type="button" onclick="getSpeechData(\''
                    + speechID + '\')" class="speech-vis-sidebar-button">' + speechID + '</button>';
            }
            document.getElementById("button-list").innerHTML = finalHTML;
        } catch (e) {
            console.error(e);
        }
    }
    req.send();
}

/**
 * Accesses speech data on button press and changes the displayed speech on the web page.
 * @param speechID The speech ID to <tt>GET</tt> data for.
 * @author Eric Lakhter
 */
function getSpeechData(speechID = "ID") {
    setPageStatus("Auf Antwort von DB warten");
    let req = new XMLHttpRequest();
    req.open("GET", "/reden/speechVis/?speechID=" + speechID);
    req.responseType = "json";
    req.onload = function () {
        try {
            // happens if speechID doesn't correspond to an existing speech
            if (req.response["speaker"] === undefined) {
                setPageStatus("Die Rede mit dieser ID ist leer.");
            } else {
                setPageSpeechVis(req.response);
            }
        } catch (e) {
            console.error(e);
            setPageStatus("Ein Fehler ist aufgetreten.");
        }
    }
    req.send();
}

/**
 * Sets the page to its default configuration.
 * @author Eric Lakhter
 */
function setPageStatus(textMessage = "") {
    document.getElementById("speech-title").innerHTML = '';
    document.getElementById("speech-header").innerHTML = '';
    document.getElementById("speech-text").innerText = textMessage;
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
    commentData.sort((a, b) => {
        return parseInt(a["id"].split("/")[1]) - parseInt(b["id"].split("/")[1])
    });
    let fullName = speakerData["firstName"] + ' ' + speakerData["lastName"];
    document.getElementById("speech-title").innerHTML = 'Rede ' + speechData["speechID"] + ' von ' + fullName;
    document.getElementById("speech-header").innerHTML =
        '<li>Redner: ' + fullName + '</li>' +
        '<li>Partei: ' + speakerData["party"] + '</li>' +
        '<li>Datum: ' + speechData["date"] + '</li>' +
        '<li>Durchschnittliches Sentiment: ' + speechData["speechSentiment"].toFixed(4) + '</li>';
    document.getElementById("speaker-pic").innerHTML =
        '<img alt="Profilbild" src="' + speakerData["picture"][0] + '" class="speaker-pic-header">';
    document.getElementById("speech-text").innerHTML = applyDataToSpeech(
        speechData["text"],
        perData,
        orgData,
        locData,
        sentenceData,
        commentData
    );
}
