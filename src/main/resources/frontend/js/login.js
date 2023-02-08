

async function login() {
    let username = document.getElementById("Username").value;

    let text = sha1(document.getElementById("Pw").value + document.getElementById("username"));
    console.log(text);
    pw = text;

    let response = await fetch("/post/applicationDataLogin/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({name: username, pw: pw})
    })
    console.log(response)
    let loginResult = await response.json()

    if (loginResult.loginSuccess===true){

        document.cookie = "key=" + loginResult.cookie;
        console.log(document.cookie);

    } else {
        document.cookie = "key=";
        console.log(document.cookie)
        window.alert(loginResult.cookie);
    }

    return false;
}


async function register() {
    let name = document.getElementById("Name").value;
    let rank = document.getElementById("Rank").value;

    var text = sha1(document.getElementById("Password").value  + document.getElementById("username"));

    password = text;

    let response = await fetch("/post/applicationDataRegister/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({name: name, pw: password, rank: rank})

    })
    console.log(response)

    return false;
}


async function changePw() {


    let oldPassword = sha1(document.getElementById("OldPw").value + document.getElementById("username"));
    let newPassword = sha1(document.getElementById("NewPw").value + document.getElementById("username"));


    let response = await fetch("/post/applicationDataPwChange/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({oldPw: oldPassword, newPw: newPassword, cookie: document.cookie.split("=")[1]})
    })
    console.log(response)
    document.cookie = "key="

    return false;
}

function logout() {

    fetch("/post/applicationDataLogoutUser/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({logoutUser: document.cookie.split("=")[1]})

    })

    document.cookie="key=";
    console.log(document.cookie)

    return false;
}

async function deleteUser() {
    let userToDelete = document.getElementById("userToDelete").value;

    let response = await fetch("/post/applicationDataDeleteUser/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({deleteUser: userToDelete, cookie: document.cookie.split("=")[1]})

    })
    console.log(response)

    return false;
}


