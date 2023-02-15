

async function editFeature() {
    let featureToChange = document.getElementById("featureToChange").value;
    let changeRank = document.getElementById("changeRank").value;
    let response = await fetch("/post/applicationDataEditFeatures/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({featureToEdit: featureToChange, editRank: changeRank
    })});
    console.log(response);
    window.location.reload();

    let editSuccess = await response.json();
    if (editSuccess.EditSuccess===false){
        window.alert("Die Editierung des Features ist fehlgeschlangen");
    }
}