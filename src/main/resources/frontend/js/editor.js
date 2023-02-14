// Used by the protocol and LaTeX editor.

// control variables

let oldEditMode = "protocol";

let cachedProtocol = {protocolID: "", date: "", begin: "", end: "", leaders: "", aItems: ""};
let cachedAgenda =   {protocolID: "", agendaID: "", speechIDs: "", subject: ""};
let cachedSpeech =   {speechID: "", speakerID: "", text: ""};
let cachedPerson =   {personID: "", firstName: "", lastName: "", role: "", title: "", place: "",
    party: "", fraction19: "", fraction20: "", gender: "", birthDate: "", deathDate: "", birthPlace: ""};

const protocolEditHTML =
    '<div class="input-row"><label class="editor-label" for="input1">Protokoll-ID*</label><input id="input1" class="editor-input" tabindex="1" onkeydown="advanceOnEnter(this)" placeholder="1/1"></div>' +
    '<div class="input-row"><label class="editor-label" for="input2">Datum*</label><input id="input2" class="editor-input" tabindex="2" onkeydown="advanceOnEnter(this)" type="date"></div>' +
    '<div class="input-row"><label class="editor-label" for="input3">Beginn*</label><input id="input3" class="editor-input" tabindex="3" onkeydown="advanceOnEnter(this)" type="time"></div>' +
    '<div class="input-row"><label class="editor-label" for="input4">Ende*</label><input id="input4" class="editor-input" tabindex="4" onkeydown="advanceOnEnter(this)" type="time"></div>' +
    '<div class="input-row"><label class="editor-label" for="input5">Sitzungsleiter*</label><input id="input5" class="editor-input" tabindex="5" onkeydown="advanceOnEnter(this)" placeholder="Präsident Max Mustermann, Vizepräsident Dr. Bob Baumeister"></div>' +
    '<div class="input-row"><label class="editor-label" for="input6">Tagesordnungspunkte*</label><input id="input6" class="editor-input" tabindex="6" onkeydown="advanceOnEnter(this)" placeholder="Tagesordnungspunkt 1, Zusatzpunkt 1"></div>';
const agendaEditHTML =
    '<div class="input-row"><label class="editor-label" for="input1">Protokoll-ID*</label><input id="input1" class="editor-input" tabindex="1" onkeydown="advanceOnEnter(this)" placeholder="1/1"></div>' +
    '<div class="input-row"><label class="editor-label" for="input2">Titel*</label><input id="input2" class="editor-input" tabindex="2" onkeydown="advanceOnEnter(this)" placeholder="Tagesordnungspunkt 1"></div>' +
    '<div class="input-row"><label class="editor-label" for="input3">Reden-IDs*</label><input id="input3" class="editor-input" tabindex="3" onkeydown="advanceOnEnter(this)" placeholder="ID100100, ID100200"></div>' +
    '<div class="input-row"><label class="editor-label" for="input4">Themen</label><textarea id="input4" class="editor-input" tabindex="4" placeholder="Reden über:\n-Wichtige Themen" rows="5"></textarea>';
const speechEditHTML =
    '<div class="input-row"><label class="editor-label" for="input1">Rede-ID*</label><input id="input1" class="editor-input" tabindex="1" onkeydown="advanceOnEnter(this)" placeholder="1/1"></div>' +
    '<div class="input-row"><label class="editor-label" for="input2">Redner-ID*</label><input id="input2" class="editor-input" tabindex="2" onkeydown="advanceOnEnter(this)" placeholder="1/1"></div>' +
    '<div class="input-row"><label class="editor-label" for="input3">Text*</label><textarea id="input3" class="editor-input" tabindex="3" placeholder="Es war einmal...\n[KOMMENTAR]Heiterkeit" rows="10"></textarea></div>';
const personEditHTML =
    '<div class="input-row"><label class="editor-label" for="input1">Person-ID*</label><input id="input1" class="editor-input" tabindex="1" onkeydown="advanceOnEnter(this)" placeholder="123456"></div>' +
    '<div class="input-row"><label class="editor-label" for="input2">Vorname*</label><input id="input2" class="editor-input" tabindex="2" onkeydown="advanceOnEnter(this)" placeholder="Max"></div>' +
    '<div class="input-row"><label class="editor-label" for="input3">Nachname*</label><input id="input3" class="editor-input" tabindex="3" onkeydown="advanceOnEnter(this)" placeholder="Mustermann"></div>' +
    '<div class="input-row"><label class="editor-label" for="input4">Rolle</label><input id="input4" class="editor-input" tabindex="4" onkeydown="advanceOnEnter(this)" placeholder="Präsident"></div>' +
    '<div class="input-row"><label class="editor-label" for="input5">Titel</label><input id="input5" class="editor-input" tabindex="5" onkeydown="advanceOnEnter(this)" placeholder="Dr."></div>' +
    '<div class="input-row"><label class="editor-label" for="input6">Ortszusatz</label><input id="input6" class="editor-input" tabindex="6" onkeydown="advanceOnEnter(this)" placeholder="1/1"></div>' +
    '<div class="input-row"><label class="editor-label" for="input7">Partei*</label><input id="input7" class="editor-input" tabindex="7" onkeydown="advanceOnEnter(this)" placeholder="SPD"></div>' +
    '<div class="input-row"><label class="editor-label" for="input8">Fraktion WP 19**</label><input id="input8" class="editor-input" tabindex="8" onkeydown="advanceOnEnter(this)" placeholder="SPD"></div>' +
    '<div class="input-row"><label class="editor-label" for="input9">Fraktion WP 20**</label><input id="input9" class="editor-input" tabindex="9" onkeydown="advanceOnEnter(this)" placeholder="SPD"></div>' +
    '<div class="input-row"><label class="editor-label" for="input10">Geschlecht</label><input id="input10" class="editor-input" tabindex="10" onkeydown="advanceOnEnter(this)" placeholder="männlich"></div>' +
    '<div class="input-row"><label class="editor-label" for="input11">Geburtsdatum</label><input id="input11" class="editor-input" tabindex="11" onkeydown="advanceOnEnter(this)" type="date"></div>' +
    '<div class="input-row"><label class="editor-label" for="input12">Sterbedatum</label><input id="input12" class="editor-input" tabindex="12" onkeydown="advanceOnEnter(this)" type="date"></div>' +
    '<div class="input-row"><label class="editor-label" for="input13">Geburtsort</label><input id="input13" class="editor-input" tabindex="13" onkeydown="advanceOnEnter(this)" placeholder="Berlin"></div>';

const protocolExplanationHTML =
    'Pflichtfelder sind mit einem * markiert.' +
    '<ul>' +
    '<li>Protokoll-IDs haben das Format "&lt;Wahlperiode&gt;/&lt;Zahl&gt;"</li>' +
    '</ul>';
const agendaExplanationHTML =
    'Pflichtfelder sind mit einem * markiert.<br>' +
    'Tagesordnungspunkte können nur eingefügt werden, falls ein Protokoll mit diesem Tagesordnungspunkt definiert wurde.' +
    '<ul>' +
    '<li>Protokoll-IDs haben das Format "&lt;Wahlperiode&gt;/&lt;Zahl&gt;"</li>' +
    '</ul>';
const speechExplanationHTML =
    'Pflichtfelder sind mit einem * markiert.<br>' +
    'Reden können nur eingefügt werden, falls irgendein Tagesordnungspunkt diese Rede-ID enthält.' +
    '<ul>' +
    '<li>Rede-IDs haben das Format "ID&lt;Wahlperiode&gt;&lt;Zahl&gt;"</li>' +
    '</ul>';
const personExplanationHTML =
    'Pflichtfelder sind mit einem * markiert.<br>' +
    '<ul>' +
    '<li>Personen-IDs sind einfach nur Zahlen: "&lt;Zahl&gt;"</li></li>' +
    '</ul>';


/**
 * Replaces the "button-list" list with all protocols and buttons to load them.
 * @author Eric Lakhter
 */
function setProtocolEditorButtons() {
    let finalHTML = '';
    for (let protocolID of protocolKeys) {
        finalHTML +=
            '<li><button onclick="setAgendaEditorButtons(\'' + protocolID + '\')"> Protokoll ' + protocolID + '</button>' +
            '<button onclick="pasteDataIntoEditor(\'protocol\', \'' + protocolID + '\')" style="float: right">Lade dieses Protokoll</button>';
    }
    document.getElementById("button-list").innerHTML = finalHTML;
}

/**
 * Replaces the "button-list" list with all agenda points of that protocol.
 * @param protocolID Protocol ID by which to find the agenda points.
 * @author Eric Lakhter
 */
function setAgendaEditorButtons(protocolID = "1/1") {
    let finalHTML = '';
    for (let agendaID of protocols[protocolID]) {
        finalHTML +=
            '<li><button type="button" onclick="setSpeechEditorButtons(\'' + agendaID + '\')">' + agendaID.split("/")[2]+ '</button>' +
            '<button onclick="pasteDataIntoEditor(\'aItem\', \'' + agendaID + '\')" style="float: right">Lade diesen TOP</button>';
    }
    document.getElementById("button-list").innerHTML = finalHTML;
}

/**
 * Replaces the "button-list" list with all speeches held during the given agenda point.
 * @param agendaID Agenda point ID by which to find the speeches.
 * @author Eric Lakhter
 */
function setSpeechEditorButtons(agendaID = "1/1/ID") {
    let finalHTML = '';
    for (let speechID of agendaItems[agendaID]["speechIDs"]) {
        finalHTML +=
            '<li><button type="button">' + speechID + '</button>' +
            '<button onclick="pasteDataIntoEditor(\'speech\', \'' + speechID + '\')" style="float: right">Lade diese Rede</button>';
    }
    document.getElementById("button-list").innerHTML = finalHTML;
}

/**
 * Replaces the "button-list" list with all people in the database.
 * @author Eric Lakhter
 */
function setPersonEditorButtons() {
    let finalHTML = '';
    for (let personID of personIDs) {
        finalHTML +=
            '<li><button type="button" style="display: flex; width: 100%" ' +
            'onclick="pasteDataIntoEditor(\'person\', \'' + personID + '\')">' +
            '<span style="text-align: left">' + people[personID]["fullName"] + ',<br> ' + people[personID]["party"] + '</span>' +
            '<span style="margin-left: auto">Lade diese Person</span></button>';
    }
    document.getElementById("button-list").innerHTML = finalHTML;
}
/**
 * Tries to insert data into the database based on user input.
 * @author Eric Lakhter
 */
async function parseDataFromEditor() {
    try {
        let editMode = document.querySelector('input[name="edit-mode"]:checked').value;
        let overwrite = document.getElementById("overwrite-checkbox").checked;
        let requestBody = getCurrentDataAsJSON(editMode);
        let response = await fetch("insert/?editMode=" + editMode + "&overwrite=" + overwrite, {
            method: 'POST',
            body: JSON.stringify(requestBody)
        });
        let responseJson = await response.json();
        if (responseJson.status === "Error")
            displayError(responseJson.details);
        else
            displaySuccess(responseJson.status);
    } catch (e) {
        console.error(e);
    }
}

/**
 * Tries to insert data into the database based on user input.
 * @author Eric Lakhter
 */
async function pasteDataIntoEditor(col = "", id = "") {
    try {
        let response = await fetch("extract/?col=" + col + "&id=" + id, {method: 'POST'});
        let responseJson = await response.json();
        if (responseJson.status === "Error") {
            displayError(responseJson.details);
            return;
        }
        document.querySelector('input[value=' + col + ']').checked = true;
        changeLayout();
        fillWithData(col, responseJson.details);
        displaySuccess(responseJson.status);
    } catch (e) {
        console.error(e);
    }
}

/**
 * Displays the error message in both the status box and the web console.
 * @param errorDetails The message to be displayed.
 * @author Eric Lakhter
 */
function displayError(errorDetails = "null") {
    let statusBox = document.getElementById("status-message-box");
    statusBox.innerHTML += '<span style="color: red">[ERROR]: ' + errorDetails + '</span><br>';
    statusBox.scrollTop = statusBox.scrollHeight;
    console.error(errorDetails);
}

/**
 * Displays the success message in both the status box and the web console.
 * @param successStatus The message to be displayed.
 * @author Eric Lakhter
 */
function displaySuccess(successStatus = "null") {
    let statusBox = document.getElementById("status-message-box");
    statusBox.innerHTML += successStatus + '<br>';
    statusBox.scrollTop = statusBox.scrollHeight;
    console.log(successStatus);
}

/**
 * Changes the editor's layout according to the button pressed.<br>
 * Preserves the previous data and fills the new interface with potentially existing old related data.
 * @author Eric Lakhter
 */
function changeLayout() {
    try {
        switch (oldEditMode) {
            case "protocol":
                cachedProtocol = getCurrentDataAsJSON(oldEditMode);
                break;
            case "aItem":
                cachedAgenda = getCurrentDataAsJSON(oldEditMode);
                break;
            case "speech":
                cachedSpeech = getCurrentDataAsJSON(oldEditMode);
                break;
            case "person":
                cachedPerson = getCurrentDataAsJSON(oldEditMode);
                break;
            default:
                break;
        }
    } catch (e) {
        console.error("An error occurred while saving the editor inputs for later");
    }
    oldEditMode = document.querySelector('input[name="edit-mode"]:checked').value;
    switch (oldEditMode) {
        case "protocol":
            fillWithData(oldEditMode, cachedProtocol);
            break;
        case "aItem":
            fillWithData(oldEditMode, cachedAgenda);
            break;
        case "speech":
            fillWithData(oldEditMode, cachedSpeech);
            break;
        case "person":
            fillWithData(oldEditMode, cachedPerson);
            break;
        default: break;
    }
}

/**
 * Returns the currently displayed editorial data as a JSON.
 * @param editMode The current editing mode.
 * @return {object} A JSON with the currently active data.
 * @author Eric Lakhter
 */
function getCurrentDataAsJSON(editMode = "") {
    let data = {};
    if (document.getElementById("input1") === null) throw Error("There doesn't seem to be an editor active");

    switch (editMode) {
        case "protocol":
            data = {
                protocolID : document.getElementById("input1").value,
                date : document.getElementById("input2").value,
                begin : document.getElementById("input3").value,
                end : document.getElementById("input4").value,
                leaders : document.getElementById("input5").value,
                aItems : document.getElementById("input6").value
            };
            break;
        case "aItem":
            data = {
                protocolID : document.getElementById("input1").value,
                agendaID : document.getElementById("input2").value,
                speechIDs : document.getElementById("input3").value,
                subject : document.getElementById("input4").value
            };
            break;
        case "speech":
            data = {
                speechID : document.getElementById("input1").value,
                speakerID : document.getElementById("input2").value,
                text : document.getElementById("input3").value
            };
            break;
        case "person":
            data = {
                personID : document.getElementById("input1").value,
                firstName : document.getElementById("input2").value,
                lastName : document.getElementById("input3").value,
                role : document.getElementById("input4").value,
                title : document.getElementById("input5").value,
                place : document.getElementById("input6").value,
                party : document.getElementById("input7").value,
                fraction19 : document.getElementById("input8").value,
                fraction20 : document.getElementById("input9").value,
                gender : document.getElementById("input10").value,
                birthDate : document.getElementById("input11").value,
                deathDate : document.getElementById("input12").value,
                birthPlace : document.getElementById("input13").value
            };
            break;
        default: break;
    }
    return data;
}

/**
 * Fills the editor with either cached data or fresh data from the DB.
 * @param editMode Current editor layout.
 * @param data JSON with data to fill the editor with.
 * @author Eric Lakhter
 */
function fillWithData(editMode = "", data = {}) {
    switch (editMode) {
        case "protocol":
            document.getElementById("input-area").innerHTML = protocolEditHTML;
            document.getElementById("explanation").innerHTML = protocolExplanationHTML;
            document.getElementById("overwrite-label").innerText = "Erlaube das Überschreiben von bereits existierenden Protokollen?";
            document.getElementById("input1").value = checkForUndefined(data.protocolID);
            document.getElementById("input2").value = checkForUndefined(data.date);
            document.getElementById("input3").value = checkForUndefined(data.begin);
            document.getElementById("input4").value = checkForUndefined(data.end);
            document.getElementById("input5").value = checkForUndefined(data.leaders);
            document.getElementById("input6").value = checkForUndefined(data.aItems);
            break;
        case "aItem":
            document.getElementById("input-area").innerHTML = agendaEditHTML;
            document.getElementById("explanation").innerHTML = agendaExplanationHTML;
            document.getElementById("overwrite-label").innerText = "Erlaube das Überschreiben von bereits existierenden Tagesordnungspunkten?";
            document.getElementById("input1").value = checkForUndefined(data.protocolID);
            document.getElementById("input2").value = checkForUndefined(data.agendaID);
            document.getElementById("input3").value = checkForUndefined(data.speechIDs);
            document.getElementById("input4").value = checkForUndefined(data.subject);
            break;
        case "speech":
            document.getElementById("input-area").innerHTML = speechEditHTML;
            document.getElementById("explanation").innerHTML = speechExplanationHTML;
            document.getElementById("overwrite-label").innerText = "Erlaube das Überschreiben von bereits existierenden Reden?";
            document.getElementById("input1").value = checkForUndefined(data.speechID);
            document.getElementById("input2").value = checkForUndefined(data.speakerID);
            document.getElementById("input3").value = checkForUndefined(data.text);
            break;
        case "person":
            document.getElementById("input-area").innerHTML = personEditHTML;
            document.getElementById("explanation").innerHTML = personExplanationHTML;
            document.getElementById("overwrite-label").innerText = "Erlaube das Überschreiben von bereits existierenden Personen?";
            document.getElementById("input1").value = checkForUndefined(data.personID);
            document.getElementById("input2").value = checkForUndefined(data.firstName);
            document.getElementById("input3").value = checkForUndefined(data.lastName);
            document.getElementById("input4").value = checkForUndefined(data.role);
            document.getElementById("input5").value = checkForUndefined(data.title);
            document.getElementById("input6").value = checkForUndefined(data.place);
            document.getElementById("input7").value = checkForUndefined(data.party);
            document.getElementById("input8").value = checkForUndefined(data.fraction19);
            document.getElementById("input9").value = checkForUndefined(data.fraction20);
            document.getElementById("input10").value = checkForUndefined(data.gender);
            document.getElementById("input11").value = checkForUndefined(data.birthDate);
            document.getElementById("input12").value = checkForUndefined(data.deathDate);
            document.getElementById("input13").value = checkForUndefined(data.birthPlace);
            break;
        default: break;
    }
}

/**
 * Makes sure that data doesn't get displayed as "undefined".
 * @param data Data to check.
 * @return {string} Either the original data or an empty string.
 */
function checkForUndefined(data ="") {
    return data !== undefined
        ? data
        : "";
}

/**
 * Advances to the next input field on enter press.
 * @param element This HTMLElement.
 */
function advanceOnEnter(element){
    if (event.which === 13) {
        let index = element.tabIndex;
        document.getElementById("input" + (index + 1)).focus();
    }
}
