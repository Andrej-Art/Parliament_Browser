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
        let editType = document.querySelector('input[name="edit-type"]:checked').value;
        let content = document.getElementById("editor-textarea").value;
        let response = await fetch("post/?editType=" + editType, {
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
function handleResponse(responseJson = {status : "Success"}) {
    if (responseJson.status === "Error") {
        throw Error(responseJson.message);
    } else {
        console.log(responseJson.status);
    }
}
