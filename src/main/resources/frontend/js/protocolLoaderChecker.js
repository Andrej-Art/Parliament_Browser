
async function loadAll(){

    let response = await fetch("/post/applicationDataDeleteUser/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify()
    });

    window.location.reload();

}

async function parseAll(){

    let response = await fetch("/post/applicationDataDeleteUser/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify()
    });

    window.location.reload();
}

async function parseNew(){

    let response = await fetch("/post/applicationDataDeleteUser/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify()
    });

    window.location.reload();
}

async function parseSingle(){
    let userToDelete = document.getElementById("userToDelete").value;
    let response = await fetch("/post/applicationDataDeleteUser/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify()
    });

    window.location.reload();
}
