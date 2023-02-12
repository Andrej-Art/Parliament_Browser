// Used by the protocol and LaTeX editor.

/**
 * Tries to insert data into the database based on user input.
 * @author Eric Lakhter
 */
async function parseEditorData() {
    try {
        let editMode = document.querySelector('input[name="edit-mode"]:checked').value;
        let requestBody = {};
        switch (editMode) {
            case "protocol":
                requestBody = {}; break;
            case "aItem":
                requestBody = {}; break;
            case "speech":
                requestBody = {}; break;
            case "person":
                requestBody = {}; break;
            default: break;
        }
        let overwrite = document.getElementById("overwrite-checkbox").checked;
        // let content = document.getElementById("editor-textarea").value;
        let response = await fetch("insert/?editMode=" + editMode + "&overwrite=" + overwrite, {
            method: 'POST',
            body: JSON.stringify(requestBody)
        });
        let responseJson = await response.json();
        if (responseJson.status === "Error") {
            displayError(responseJson.message);
            return;
        }
        displaySuccess(responseJson.message);
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
        let response = await fetch("insert/?col=" + col + "&id=" + id, {method: 'POST'});
        let responseJson = await response.json();
        if (responseJson.status === "Error") {
            displayError(responseJson.message);
            return;
        }
        switch (responseJson.status) {
            case "protocol":
                fillWithData("protocol", responseJson.message);
                break;
            case "aItem":
                fillWithData("aItem", responseJson.message);
                break;
            case "speech":
                fillWithData("speech", responseJson.message);
                break;
            case "person":
                fillWithData("person", responseJson.message);
                break;
            default: break;
        }
        displaySuccess(responseJson.message);
    } catch (e) {
        console.error(e);
    }
}

/**
 * Displays the error message in both the status box and the web console.
 * @param errorMessage The message to be displayed.
 * @author Eric Lakhter
 */
function displayError(errorMessage = "null") {
    let statusBox = document.getElementById("status-message-box");
    statusBox.innerHTML += '<span style="color: red">[ERROR]: ' + errorMessage + '</span><br>';
    statusBox.scrollTop = statusBox.scrollHeight;
    console.error(errorMessage);
}

/**
 * Displays the success message in both the status box and the web console.
 * @param successMessage The message to be displayed.
 * @author Eric Lakhter
 */
function displaySuccess(successMessage = "null") {
    let statusBox = document.getElementById("status-message-box");
    statusBox.innerHTML += successMessage + '<br>';
    statusBox.scrollTop = statusBox.scrollHeight;
    console.log(successMessage);
}

/**
 * Changes the editor's layout according to the button pressed.<br>
 * Preserves the previous data and fills the new interface with potentially existing old related data.
 * @author Eric Lakhter
 */
function changeLayout() {
    switch (oldEditMode) {
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
    oldEditMode = document.querySelector('input[name="edit-mode"]:checked').value;
    switch (oldEditMode) {
        case "protocol":
            fillWithData("protocol", cachedProtocol);
            break;
        case "aItem":
            fillWithData("aItem", cachedAgenda);
            break;
        case "speech":
            fillWithData("speech", cachedSpeech);
            break;
        case "person":
            fillWithData("person", cachedPerson);
            break;
        default: break;
    }
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
            // let cachedProtocol = {protocolID: "", date: "", begin: "", end: "", leaders: "", aItems: ""};
            break;
        case "aItem":
            document.getElementById("input-area").innerHTML = agendaEditHTML;
            // let cachedAgenda =   {protocolID: "", agendaID: "", subject: "", speechIDs: ""};
            break;
        case "speech":
            document.getElementById("input-area").innerHTML = speechEditHTML;
            // let cachedSpeech =   {speechID: "", speakerID: "", text: ""};
            break;
        case "person":
            document.getElementById("input-area").innerHTML = personEditHTML;
            // let cachedPerson =   {personID: "", firstName: "", lastName: "", role: "", title: "", place: "",
            //     fraction19: "", fraction20: "", party: "", gender: "", birthDate: "", deathDate: "", birthPlace: ""};
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

let oldEditMode = "protocol";

let cachedProtocol = {protocolID: "", date: "", begin: "", end: "", leaders: "", aItems: ""};
let cachedAgenda =   {protocolID: "", agendaID: "", subject: "", speechIDs: ""};
let cachedSpeech =   {speechID: "", speakerID: "", text: ""};
let cachedPerson =   {personID: "", firstName: "", lastName: "", role: "", title: "", place: "",
    fraction19: "", fraction20: "", party: "", gender: "", birthDate: "", deathDate: "", birthPlace: ""};

const protocolEditHTML =
    '<div class="input-column"><label for="input-1">Protokoll-ID</label>' +
    '<input id="input-1" tabindex="1" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1"></div>' +
    '<div class="input-column"><label for="input-2">Datum</label>' +
    '<input id="input-2" tabindex="2" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="01.01.2000"></div>' +
    '<div class="input-column"><label for="input-3">Beginn</label>' +
    '<input id="input-3" tabindex="3" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="9:00"></div>' +
    '<div class="input-column"><label for="input-4">Ende</label>' +
    '<input id="input-4" tabindex="4" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="21:00"></div>' +
    '<div class="input-column"><label for="input-5">Sitzungsleiter</label>' +
    '<input id="input-5" tabindex="5" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="Präsident Max Mustermann, Vizepräsident Dr. Bob Baumeister"></div>' +
    '<div class="input-column"><label for="input-6">Tagesordnungspunkte</label>' +
    '<input id="input-6" tabindex="6" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="Tagesordnungspunkt 1, Zusatzpunkt 1"></div>' +
    '</div>';
const agendaEditHTML =
    '<div class="input-column"><label for="input-1">Protokoll-ID</label>' +
    '<input id="input-1" tabindex="1" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1"></div>' +
    '<div class="input-column"><label for="input-2">Titel</label>' +
    '<input id="input-2" tabindex="2" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="Tagesordnungspunkt 1"></div>' +
    '<div class="input-column"><label for="input-3">Reden-IDs</label>' +
    '<input id="input-3" tabindex="3" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="ID100100, ID100200"></div>' +
    '<div class="input-column"><label for="input-4">Themen</label>' +
    '<textarea id="input-4" tabindex="4" class="editor-input" placeholder="Reden über:\n-Wichtige Themen" rows="5"></textarea>' +
    '</div>';
const speechEditHTML =
    '<div class="input-column"><label for="input-1">Rede-ID</label>' +
    '<input id="input-1" tabindex="1" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1"></div>' +
    '<div class="input-column"><label for="input-2">Redner-ID</label>' +
    '<input id="input-2" tabindex="2" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1"></div>' +
    '<div class="input-column"><label for="input-3">Text</label>' +
    '<textarea id="input-3" tabindex="3" class="editor-input" placeholder="Es war einmal...\n[KOMMENTAR]Heiterkeit" rows="5"></textarea></div>'
const personEditHTML =
    '<div class="input-column"><label for="input-1">Person-ID</label>' +
    '<input id="input-1" tabindex="1" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="123456"></div>' +
    '<div class="input-column"><label for="input-2">Vorname</label>' +
    '<input id="input-2" tabindex="2" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="Max"></div>' +
    '<div class="input-column"><label for="input-3">Nachname</label>' +
    '<input id="input-3" tabindex="3" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="Mustermann"></div>' +
    '<div class="input-column"><label for="input-4">Rolle</label>' +
    '<input id="input-4" tabindex="4" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1"></div>' +
    '<div class="input-column"><label for="input-5">Titel</label>' +
    '<input id="input-5" tabindex="5" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1"></div>' +
    '<div class="input-column"><label for="input-6">Ortszusatz</label>' +
    '<input id="input-6" tabindex="6" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1"></div>' +
    '<div class="input-column"><label for="input-7">Partei</label>' +
    '<input id="input-7" tabindex="7" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1"></div>' +
    '<div class="input-column"><label for="input-8">Fraktion WP 19</label>' +
    '<input id="input-8" tabindex="8" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1"></div>' +
    '<div class="input-column"><label for="input-9">Fraktion WP 20</label>' +
    '<input id="input-9" tabindex="9" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1"></div>' +
    '<div class="input-column"><label for="input-10">Geschlecht</label>' +
    '<input id="input-10" tabindex="10" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1"></div>' +
    '<div class="input-column"><label for="input-11">Geburtsdatum</label>' +
    '<input id="input-11" tabindex="11" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1"></div>' +
    '<div class="input-column"><label for="input-12">Sterbedatum</label>' +
    '<input id="input-12" tabindex="12" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1"></div>' +
    '<div class="input-column"><label for="input-13">Geburtsort</label>' +
    '<input id="input-13" tabindex="13" onkeydown="advanceOnEnter(this)" class="editor-input" placeholder="1/1"></div>' +
    '</div>';
