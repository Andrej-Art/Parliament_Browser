

async function login() {
    let username = document.getElementById("Username").value;
    let pw = sha1(document.getElementById("Pw").value);
    console.log(pw);
    let response = await fetch("/post/applicationDataLogin/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({name: username, pw: pw})
    });
    console.log(response);
    let loginResult = await response.json();
    if (loginResult.loginSuccess===true){
        document.cookie = "key=" + loginResult.cookie + ";path=/";
        console.log(document.cookie);
    } else {
        document.cookie = "key=" + "; path=/";
        console.log(document.cookie);
        window.alert(loginResult.cookie);
    }
    window.location.reload();
}



async function changePw() {
    let oldPassword = sha1(document.getElementById("OldPw").value);
    if(document.getElementById("newPW").value==="") {
        window.alert("Das Passwort ist zu kurz");
    } else {
        let newPassword = sha1(document.getElementById("NewPw").value);
        let response = await fetch("/post/applicationDataPwChange/", {
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                oldPw: oldPassword,
                newPw: newPassword,
                cookie: document.cookie.split(";")[0].split("=")[1]
            })
        });
        logout(document.cookie);
        document.cookie = "key=" + "; path=/";
        window.location.reload();

        let changeSuccess = await response.json();
        if (changeSuccess.changeSuccess===false){
            window.alert("Die Änderung des Passwortes ist fehlgeshlangen");
        }
    }
}

function logout() {
    fetch("/post/applicationDataLogoutUser/", {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({logoutUser: document.cookie.split(";")[0].split("=")[1]})
    });
    document.cookie="key=" + "; path=/";
    console.log(document.cookie);
    window.location.reload();
}


