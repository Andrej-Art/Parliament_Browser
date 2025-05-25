<!DOCTYPE html>
<html lang="de">
<!DOCTYPE html>
<html>
<head>
    <!-- source: https://iq.opengenus.org/sha1-algorithm/ -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/js-sha1/0.6.0/sha1.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <meta name="author" content="Julian Ocker">
    <title>Login-/User-Management</title>
    <style>
        <#include "css/parliamentBrowser.css">
    </style>
</head>
<br>
<#include "parliamentBrowser.ftl">
<br>
<#if loginStatus==false >
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
</#if>
<#if loginStatus==true >
    <h3>Logout</h3>
    <div>
        <form onsubmit="logout();">
            <div>
                <br>
                <button type="submit">logout</button>
            </div>
        </form>
    </div>
    <br>
    <br>


    <h3>Passwort ändern</h3>

    <div>
        <form onsubmit="changePw();">
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

</#if>
<#if addUserRight==true>

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

</#if>
<#if deleteUserRight==true>

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
    <br>
    <br>

</#if>
<#if editUserRight==true>

    <h3>Editiere einen neuen User</h3>
    <div>
        <form onsubmit="editUserAccess(); return false;">
            <div>
                <select id="userToChange">
                    <#list userList as user>
                        <option value=${user.getID()}>
                            ${user.getID()}, ${user.getRank()}
                        </option>
                    </#list>
                </select>
            </div>
            <br>
            <h4>Bitte füllen Sie nur die Felder aus deren Inhalt sie ändern wollen</h4>
            <br>
            <div>
                Username
                <br>
                <input id="ChangeName" minlength="4" maxlength="128">
            </div>
            <div>
                Passwort
                <br>
                <input type="password" id="ChangePassword" minlength="8" maxlength="32">
            </div>
            <div>
                Rang
                <br>
                <select selected="" id="ChangeRank">
                    <option value="">
                        (nicht ändern)
                    </option>
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
                <button type="submit">Editieren</button>
            </div>
        </form>
    </div>
</#if>

</html>
<script>
    <#include "js/login.js">
</script>
<script>
    <#include "js/userManagement.js">
</script>

</html>