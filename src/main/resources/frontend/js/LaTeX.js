
/**
 * Tries to generate a pdf based on user input.
 * @author Eric Lakhter
 * @author DavidJordan
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


function createProtocolButtons() {
    let finalHTML = '';
    for (let protID of protIDs) {
        finalHTML += '<li><button type="button" onclick="getProtocolData(\'' + protID +  '\')" class="pdf-export-protoc-button"> Protokoll ' + protID + '</button>';
    }
    document.getElementById("button-list").innerHTML = finalHTML;

}


function getProtocolData(protocID){
    // let ID = protocID.replace("Protokoll ", "");
    // $('#status-message-box').text('Waiting for response from DB ...');
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
        }
    }
    req.send();
}

function insertLatexString(data = {}){
    document.getElementById("editor-textarea").innerHTML = data["latexString"];
}
