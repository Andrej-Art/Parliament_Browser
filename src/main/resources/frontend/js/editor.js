// Used by the protocol and LaTeX editor.

/**
 * Tries to insert data into the database based on user input.
 * @author Eric Lakhter
 */
async function parseContent() {
    try {
        let editType = document.querySelector('input[name="edit-mode"]:checked').value;
        let content = document.getElementById("editor-textarea").value;
        let response = await fetch("?editMode=" + editType, {
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

const protocolDefault =
    '[PROTOKOLL]19/1\n' +
    '[DATUM]01.10.2000\n' +
    '[BEGINN]9:00\n' +
    '[ENDE]21:00\n' +
    '[SITZUNGSLEITER]Präsident Max Mustermann, Vizepräsident Dr. Bob Baumeister\n' +
    '[TOPS]Tagesordnungspunkt 1, Zusatzpunkt 1';
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

/**
 * Changes the default text in the protocol editing field depending on editing mode.
 * @author Eric Lakhter
 */
function changeDefaultText() {
    let editType = document.querySelector('input[name="edit-mode"]:checked').value;
    switch (editType) {
        case "protocol":
            document.getElementById("editor-textarea").placeholder = protocolDefault;
            break;
        case "aItem":
            document.getElementById("editor-textarea").placeholder = agendaDefault;
            break;
        case "speech":
            document.getElementById("editor-textarea").placeholder = speechDefault;
            break;
        default: break;
    }
}
