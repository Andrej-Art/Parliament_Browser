<!DOCTYPE html>
<html lang="de">
<html>
<head>
    <!-- source: https://iq.opengenus.org/sha1-algorithm/ -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/js-sha1/0.6.0/sha1.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <meta name="author" content="Julian Ocker">
    <title>Feature-Management</title>
    <style>
        <#include "css/parliamentBrowser.css">
    </style>
</head>
<br>
<#include "parliamentBrowser.ftl">
<br>
<#if editFeatureRight==true>
    <h3>Editieren der Features</h3>
    <div>
        <form onsubmit="editFeature(); return false;">
            <div>
                Feature das editiert werden soll
                <br>
                <select id="featureToChange">
                    <#list featureList as feature>
                        <option value=${feature}>
                            ${feature}
                        </option>
                    </#list>
                </select>
            </div>
            <br>
            <div>
                Rang ab dem das Feature verfügbar sein soll.
                <br>
                <select selected="" id="changeRank">
                    <option value="">
                        (nicht ändern)
                    </option>
                    <option value="everyone">
                        Jeder
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
                    <option value="nobody">
                        Niemand
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

<script>
    <#include "js/feature.js">
</script>
</html>