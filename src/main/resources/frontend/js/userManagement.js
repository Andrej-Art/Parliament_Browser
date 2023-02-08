
async function register() {
    let name = document.getElementById("Name").value;
    let rank = document.getElementById("Rank").value;
    let password = sha1(document.getElementById("Password").value  + document.getElementById("username"));
    let response = await fetch("/post/applicationDataRegister/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({name: name, pw: password, rank: rank})
    });
    console.log(response);
}

async function deleteUser() {
    let userToDelete = document.getElementById("userToDelete").value;
    let response = await fetch("/post/applicationDataDeleteUser/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({deleteUser: userToDelete, cookie: document.cookie.split(";")[0].split("=")[1]})
    });
    console.log(response);
}


