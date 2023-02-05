// Used by the protocol and latex editor.

/**
 * Tries to generate a pdf based on user input.
 * @author Eric Lakhter
 */
async function parseLaTeX() {
    try {
        let content = document.getElementById("editor-textarea").value;
        let response = await fetch("post/", {
            method: 'POST',
            body: content
        });
        handleResponse(await response.json());
        document.getElementById("editor-preview").src = "/reden/";
    } catch (e) {
        console.error(e);
    }
}

/**
 * Tries to insert data into the database based on user input.
 * @author Eric Lakhter
 */
async function parseContent() {
    try {
        let editType = document.querySelector('input[name="edit-mode"]:checked').value;
        let content = document.getElementById("editor-textarea").value;
        let response = await fetch("post/?editMode=" + editType, {
            method: 'POST',
            body: content
        });
        handleResponse(await response.json());
    } catch (e) {
        console.error(e);
    }
}

/**
 * Handles the response's body.
 * @param responseJson Response body as JSON.
 * @author Eric Lakhter
 */
function handleResponse(responseJson = {status : "Success", message : "Successfully did a thing"}) {
    console.log(responseJson.status);
    if (responseJson.status === "Success") {
        let textArea = document.getElementById("status-message-box");
        textArea.value += '\n' + responseJson.message;
        textArea.scrollTop = textArea.scrollHeight;
    } else {
        throw Error(responseJson.message);
    }
}

/**
 * Changes the default text in the protocol editing field depending on editing mode.
 * @author Eric Lakhter
 */
function changeDefaultText() {
    let editType = document.querySelector('input[name="edit-mode"]:checked').value;
    switch (editType) {
        case "protocol":
            document.getElementById("editor-textarea").placeholder =
                '[PROTOKOLL]19/1\n' +
                '[DATUM]01.10.2000\n' +
                '[BEGINN]9:00\n' +
                '[ENDE]21:00\n' +
                '[SITZUNGSLEITER]Präsident Max Mustermann, Vizepräsident Dr. Bob Baumeister\n' +
                '[TOPS]Tagesordnungspunkt 1, Zusatzpunkt 1';
            break;
        case "aItem":
            document.getElementById("editor-textarea").placeholder =
                '[PROTOKOLL]19/1\n' +
                '[TOP]Tagesordnungspunkt 1\n' +
                '[INHALT]Reden über Zeug:\n' +
                '[INHALT]- Rede von Max Mustermann\n' +
                '[INHALT]- Rede von Dr. Bob Baumeister\n' +
                '[REDEID]ID1900001, ID1900002, ID1900003';
            break;
        case "speech":
            document.getElementById("editor-textarea").placeholder =
                '[PROTOKOLL]19/1\n' +
                '[TOP]Tagesordnungspunkt 1\n' +
                '[REDEID]ID1234567\n' +
                '[REDNERID]11001100\n' +
                'Mein sehr geehrten Damen und Herren!\n' +
                '[KOMMENTAR]Heiterkeit';
            break;
        default: break;
    }
}
