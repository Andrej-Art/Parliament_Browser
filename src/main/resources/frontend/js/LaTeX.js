
/**
 * Tries to generate a pdf based on user input.
 * @author DavidJordan, Eric Lakhter
 */
async function parseLaTeX() {
    document.getElementById('status-message-box').innerHTML = "Attempting to convert Latex String to .pdf file";
    try {
        let content = document.getElementById("editor-textarea").value;
        let response = await fetch("#", {
            method: 'POST',
            body: content
        });
        let responseJson = await response.json();
        //handleResponse(responseJson);
        document.getElementById("editor-preview").src = responseJson["message"];
        document.getElementById('status-message-box').innerHTML = "Successful compilation. .pdf ready for download.";
    } catch (e) {
        console.error(e);
        document.getElementById('status-message-box').innerHTML = "Error. The String could not be compiled."
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
 * Function that creates the protocol buttons from which the User can choose.
 * @author DavidJordan
 */
function createProtocolButtons() {
    let finalHTML = '';
    for (let protID of protIDs) {
        finalHTML += '<li><button type="button" onclick="getProtocolData(\'' + protID +  '\')" class="pdf-export-protoc-button"> Protokoll ' + protID + '</button>';
    }
    document.getElementById("button-list").innerHTML = finalHTML;

}

/**
 * Function which delivers the LaTeX formatted String with all Diagrams and Images inserted to the Editor field
 * @param protocID
 * @author DavidJordan
 */
function getProtocolData(protocID){

     document.getElementById('status-message-box').innerHTML = "Waiting for DB response...";
    let req = new XMLHttpRequest();
    let timestamp = new Date().getTime();
    console.log("the prot id is:  " + protocID)
    req.open("GET", "/latex/protocol/?protocolID=" + protocID + "&timestamp=" + timestamp);
    req.setRequestHeader('cache-control', 'no-cache');
    req.responseType = "json";
    req.onload = function () {
        try {
            insertLatexString(req.response);
            document.getElementById('status-message-box').innerHTML = "DB has delivered String to Textarea.";
        }
        catch (e) {
            console.error(e);
            document.getElementById('status-message-box').innerHTML = "Error. String of LaTeX formatted Protocol could not be delivered."
        }
    }
    req.send();
}

/**
 * Function that inserts the latexString into the textarea field.
 * @param data The query response data
 * @author DavidJordan
 */
function insertLatexString(data = {}){
    if(data["latexString"] === {}) {
        document.getElementById('status-message-box').innerHTML = "There was an error processing this speech. String not delivered. "
    }
    else {
        document.getElementById("editor-textarea").innerHTML = data["latexString"];
    }
}
