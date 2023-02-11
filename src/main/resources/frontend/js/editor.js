// Used by the protocol and LaTeX editor.

/**
 * Tries to insert data into the database based on user input.
 * @author Eric Lakhter
 */
async function parseEditorData() {
    try {
        let editType = document.querySelector('input[name="edit-mode"]:checked').value;
        let requestBody = {};
        switch (editType) {
            case "protocol":
                requestBody = {};
                break;
            case "aItem":
                requestBody = {};
                break;
            case "speech":
                requestBody = {};
                break;
            case "person":
                requestBody = {};
                break;
            default: break;
        }
        let overwrite = document.getElementById("overwrite-checkbox").checked;
        // let content = document.getElementById("editor-textarea").value;
        let response = await fetch("insert/?editMode=" + editType + "&overwrite=" + overwrite, {
            method: 'POST',
            body: JSON.stringify(requestBody)
        });
        handleResponse(await response.json());
    } catch (e) {
        console.error(e);
    }
}

/**
 * Tries to insert data into the database based on user input.
 * @author Eric Lakhter
 */
async function pasteEditorData(id = "") {
    try {
        let col = document.querySelector('input[name="edit-mode"]:checked').value;
        let response = await fetch("insert/?col=" + col + "&id=" + id, {
            method: 'POST',
            body: content
        });
        handleResponse(await response.json());
    } catch (e) {
        console.error(e);
    }
}

/**
 * Tries to generate a pdf based on user input.
 * @author Eric Lakhter
 */
async function parseLaTeX() {
    try {
        let content = document.getElementById("editor-textarea").value;
        let response = await fetch("#", {
            method: 'POST',
            body: content
        });
        let responseJson = await response.json();
        handleResponse(responseJson);
        document.getElementById("editor-preview").src = responseJson.message;
    } catch (e) {
        console.error(e);
    }
}

/**
 * Handles the response's body.
 * @param responseJson Response body as JSON.
 * @author Eric Lakhter
 */
function handleResponse(responseJson = {status : "Successfully did a thing", message : "null"}) {
    console.log(responseJson);
    let statusBox = document.getElementById("status-message-box");
    if (responseJson.status === "Error") {
        statusBox.innerHTML += '<span style="color: red">[ERROR]: ' + responseJson.message + '</span><br>';
        statusBox.scrollTop = statusBox.scrollHeight;
        console.error(responseJson.message);
    } else {
        statusBox.innerHTML += responseJson.status + '<br>';
        statusBox.scrollTop = statusBox.scrollHeight;
    }
}

/**
 * Changes the editor's layout according to the button pressed.<br>
 * Preserves the previous data and fills the new interface with potentially existing old related data.
 * @author Eric Lakhter
 */
function changeLayout() {
    switch (oldEditType) {
        case "protocol":
            cachedProtocol = {};
            break;
        case "aItem":
            cachedAgenda = {};
            break;
        case "speech":
            cachedSpeech = {};
            break;
        case "person":
            cachedPerson = {};
            break;
        default: break;
    }
    oldEditType = document.querySelector('input[name="edit-mode"]:checked').value;
    switch (oldEditType) {
        case "protocol":
            document.getElementById("input-area").innerHTML = protocolEditHTML;
            break;
        case "aItem":
            document.getElementById("input-area").innerHTML = agendaEditHTML;
            break;
        case "speech":
            document.getElementById("input-area").innerHTML = speechEditHTML;
            break;
        case "person":
            document.getElementById("input-area").innerHTML = personEditHTML;
            break;
        default: break;
    }
}

/**
 * Advances to the next input field on enter press.
 * @param element This HTMLElement.
 */
function advanceOnEnter(element){
    if (event.which === 13) {
        let index = element.tabIndex;
        document.getElementById("button-" + (index + 1)).focus();
    }
}

// control variables

const protocolDefault =
    '[PROTOKOLL]19/1\n' +
    '[DATUM]01.10.2000\n' +
    '[BEGINN]9:00\n' +
    '[ENDE]\n' +
    '[SITZUNGSLEITER]\n' +
    '[TOPS]';
const agendaDefault =
    '[PROTOKOLL]19/1\n' +
    '[TOP]Tagesordnungspunkt 1\n' +
    '[INHALT]Reden über Zeug:\n' +
    '[INHALT]- Rede von Max Mustermann\n' +
    '[INHALT]- Rede von Dr. Bob Baumeister\n' +
    '[REDEIDS]ID1900001, ID1900002, ID1900003';
const speechDefault =
    '[PROTOKOLL]19/1\n' +
    '[TOP]Tagesordnungspunkt 1\n' +
    '[REDEID]ID1234567\n' +
    '[REDNERID]11001100\n' +
    'Meine sehr geehrten Damen und Herren!\n' +
    '[KOMMENTAR]Heiterkeit';

let oldEditType = "protocol";

let cachedProtocol = {protocolID: "", date: "", begin: "", end: "", leaders: "", aItems: ""};
let cachedAgenda =   {protocolID: "", agendaID: "", subject: "", speechIDs: ""};
let cachedSpeech =   {protocolID: "", speakerID: "", text: ""};
let cachedPerson =   {personID: "", firstName: "", lastName: "", role: "", title: "", place: "",
    fraction19: "", fraction20: "", party: "", gender: "", birthDate: "", deathDate: "", birthPlace: ""};

const protocolEditHTML =
    '<div class="input-column" style="background-color: aquamarine">' +
    '<label for="button-1">Protokoll-ID</label>' +
    '<label for="button-2">Datum</label>' +
    '<label for="button-3">Beginn</label>' +
    '<label for="button-4">Ende</label>' +
    '<label for="button-5">Sitzungsleiter</label>' +
    '<label for="button-6">Tagesordnungspunkte</label>' +
    '</div>' +
    '<div class="input-column" style="background-color: #f1aa64">' +
    '<input id="button-1" tabindex="1" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1">' +
    '<input id="button-2" tabindex="2" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="01.01.2000">' +
    '<input id="button-3" tabindex="3" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="9:00">' +
    '<input id="button-4" tabindex="4" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="21:00">' +
    '<input id="button-5" tabindex="5" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="Präsident Max Mustermann, Vizepräsident Dr. Bob Baumeister">' +
    '<input id="button-6" tabindex="6" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="Tagesordnungspunkt 1, Zusatzpunkt 1">' +
    '</div>';
const agendaEditHTML = '';
const speechEditHTML = '';
const personEditHTML = '';













