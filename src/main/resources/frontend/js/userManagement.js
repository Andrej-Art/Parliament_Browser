
async function register() {
    let name = document.getElementById("Name").value;
    let rank = document.getElementById("Rank").value;
    if (name.includes(" ")) {
        window.alert("Leerzeichen im Username sind verboten");
        return;
    }
    let password = sha1(document.getElementById("Password").value );
    let response = await fetch("/post/applicationDataRegister/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({name: name, pw: password, rank: rank})
    });
    console.log(response);
    window.location.reload();
}

async function deleteUser() {
    let userToDelete = document.getElementById("userToDelete").value;
    let response = await fetch("/post/applicationDataDeleteUser/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({deleteUser: userToDelete, cookie: document.cookie.split(";")[0].split("=")[1]})
    });
    console.log(response);
    window.location.reload();
}

async function editUserAccess() {
    let userToChange = document.getElementById("userToChange").value;
    let changeName = document.getElementById("ChangeName").value;
    if (changeName.includes(" ")) {
        window.alert("Leerzeichen im Username sind verboten");
        return;
    }
    let changePassword = sha1(document.getElementById("ChangePassword").value);
    let changeRank = document.getElementById("ChangeRank").value;
    let response = await fetch("/post/applicationDataEditUser/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({editOldID: userToChange, editNewID: changeName, editPassword: changePassword,
            editRank: changeRank,cookie: document.cookie.split(";")[0].split("=")[1]})
    });
    console.log(response);
    window.location.reload();
}


