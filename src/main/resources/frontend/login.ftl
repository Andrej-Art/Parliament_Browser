<!DOCTYPE html>
<html lang="de">
<!DOCTYPE html>
<html>
<head>
    <!-- source: https://iq.opengenus.org/sha1-algorithm/ -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/js-sha1/0.6.0/sha1.min.js">

    </script>
</head>


<h3>Login</h3>
<div>
    <form onsubmit="login(); return false;">
        <div>
            Username
            <br>
            <input id="Username" minlength="4" maxlength="128">
        </div>
        <div>
            Passwort
            <br>
            <input type="password" id="Pw" maxlength="32">
        </div>
        <div>
            <br>
            <button type="submit">Login</button>
        </div>
    </form>
</div>

<br>
<br>


<h3>Logout</h3>
<div>
    <form onsubmit="logout(); return false;">
        <div>
            <br>
            <button type="submit">logout</button>
        </div>
    </form>
</div>


<br>
<br>


<h3>Registriere einen neuen User</h3>

<div>
    <form onsubmit="register(); return false;">
        <div>
            Username
            <br>
            <input id="Name" minlength="4" maxlength="128">
        </div>
        <div>
            Passwort
            <br>
            <input type="password" id="Password" minlength="8" maxlength="32">
        </div>
        <div>
            Rang
            <br>
            <select id="Rank">
                <option value="user">
                    User
                </option>
                <option value="manager">
                    Manager
                </option>
                <option value="admin">
                    Admin
                </option>
            </select>
        </div>
        <div>
            <br>
            <button type="submit">Registrieren</button>
        </div>
    </form>
</div>


<br>
<br>

<h3>Passwort ändern</h3>

<div>
    <form onsubmit="changePw(); return false;">
        <div>
            Altes Password
            <br>
            <input type="password" id="OldPw" maxlength="32">
        </div>
        <div>
            Neues Passwort
            <br>
            <input type="password" id="NewPw" minlength="8" maxlength="32">
        </div>
        <div>
            <br>
            <button type="submit">Passwort ändern</button>
        </div>
    </form>
</div>


<br>
<br>

<h3>User löschen</h3>

<div>
    <form onsubmit="deleteUser(); return false;">
        <div>
            Nutzername des zu löschenden Users.
            <br>
            <input id="userToDelete">
        </div>
        <br>
        <div>
            <button type="submit">User löschen</button>
        </div>
    </form>
</div>



</html>

<script>
    <#include "js/login.js">
</script>

</html>