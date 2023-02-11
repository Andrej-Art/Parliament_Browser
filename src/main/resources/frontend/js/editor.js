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
        document.getElementById("input-" + (index + 1)).focus();
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
    '<label for="input-1">Protokoll-ID</label>' +
    '<label for="input-2">Datum</label>' +
    '<label for="input-3">Beginn</label>' +
    '<label for="input-4">Ende</label>' +
    '<label for="input-5">Sitzungsleiter</label>' +
    '<label for="input-6">Tagesordnungspunkte</label>' +
    '</div>' +
    '<div class="input-column" style="background-color: #f1aa64">' +
    '<input id="input-1" tabindex="1" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1">' +
    '<input id="input-2" tabindex="2" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="01.01.2000">' +
    '<input id="input-3" tabindex="3" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="9:00">' +
    '<input id="input-4" tabindex="4" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="21:00">' +
    '<input id="input-5" tabindex="5" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="Präsident Max Mustermann, Vizepräsident Dr. Bob Baumeister">' +
    '<input id="input-6" tabindex="6" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="Tagesordnungspunkt 1, Zusatzpunkt 1">' +
    '</div>';
const agendaEditHTML =
    '<div class="input-column" style="background-color: aquamarine">' +
    '<label for="input-1">Protokoll-ID</label>' +
    '<label for="input-2">Titel</label>' +
    '<label for="input-3">Themen</label>' +
    '<label for="input-4">Reden-IDs</label>' +
    '</div>' +
    '<div class="input-column" style="background-color: #f1aa64">' +
    '<input id="input-1" tabindex="1" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1">' +
    '<input id="input-2" tabindex="2" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="Tagesordnungspunkt 1">' +
    '<input id="input-3" tabindex="3" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="Reden über\n-Wichtige Themen">' +
    '<input id="input-4" tabindex="4" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="ID100100, ID100200">' +
    '</div>';
const speechEditHTML =
    '<div class="input-column" style="background-color: aquamarine">' +
    '<label for="input-1">Rede-ID</label>' +
    '<label for="input-2">Redner-ID</label>' +
    '<label for="input-3">Text</label>' +
    '</div>' +
    '<div class="input-column" style="background-color: #f1aa64">' +
    '<input id="input-1" tabindex="1" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1">' +
    '<input id="input-2" tabindex="2" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1">' +
    '<textarea id="input-3" tabindex="3" class="editor-input" placeholder="Es war einmal"></textarea>' +
    '</div>';
const personEditHTML =
    '<div class="input-column" style="background-color: aquamarine">' +
    '<label for="input-1">Person-ID</label>' +
    '<label for="input-2">Vorname</label>' +
    '<label for="input-3">Nachname</label>' +
    '<label for="input-4">Rolle</label>' +
    '<label for="input-5">Titel</label>' +
    '<label for="input-6">Ortszusatz</label>' +
    '<label for="input-7">Partei</label>' +
    '<label for="input-8">Fraktion WP 19</label>' +
    '<label for="input-9">Fraktion WP 20</label>' +
    '<label for="input-10">Geschlecht</label>' +
    '<label for="input-11">Geburtsdatum</label>' +
    '<label for="input-12">Sterbedatum</label>' +
    '<label for="input-13">Geburtsort</label>' +
    '</div>' +
    '<div class="input-column" style="background-color: #f1aa64">' +
    '<input id="input-1" tabindex="1" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="123456">' +
    '<input id="input-2" tabindex="2" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="Max">' +
    '<input id="input-3" tabindex="3" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="Mustermann">' +
    '<input id="input-4" tabindex="4" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1">' +
    '<input id="input-5" tabindex="5" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1">' +
    '<input id="input-6" tabindex="6" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1">' +
    '<input id="input-7" tabindex="7" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1">' +
    '<input id="input-8" tabindex="8" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1">' +
    '<input id="input-9" tabindex="9" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1">' +
    '<input id="input-10" tabindex="10" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1">' +
    '<input id="input-11" tabindex="11" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1">' +
    '<input id="input-12" tabindex="12" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1">' +
    '<input id="input-13" tabindex="13" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1">' +
    '</div>';
