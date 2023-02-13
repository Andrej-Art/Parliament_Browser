
/**
 * Tries to generate a pdf based on user input.
 * @author Eric Lakhter
 */
async function parseLaTeX() {
    try {
        let content = document.getElementById("editor-textarea").value;
        let response = await fetch("/latex/pdf/", {
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


function createProtocolButtons() {
    let finalHTML = '';
    for (let protID of protIDs) {
        finalHTML += '<li><button type="button" onclick="getProtocolData(\'' + protID +  '\')" class="pdf-export-protoc-button"> Protokoll ' + protID + '</button>';
    }
    document.getElementById("button-list").innerHTML = finalHTML;
}


function getProtocolData(protocID){
    // let ID = protocID.replace("Protokoll ", "");
    let req = new XMLHttpRequest();
    console.log("the prot id is:  " + protocID)
    req.open("GET", "/latex/protocol/?protocolID=" + protocID);
    req.responseType = "json";
    req.onload = function () {
        try {
            insertLatexString(req.response);
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
