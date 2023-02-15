
<!-- Parliament Browser navigation bar -->

<div id="parliament-browser-main-navigation-bar-hide">
<#if !isHomepage?? || !isHomepage>
    <button class="nav-button" onclick="showStatusBar()"><i class='fa fa-angle-double-down'></i></button>
</#if>
</div>
<div id="parliament-browser-main-navigation-bar-show">
<#if !isHomepage?? || !isHomepage>
    <button class="nav-button" onclick="hideStatusBar()"><i class='fa fa-angle-double-up'></i></button>
<#else>
    <div style="width: 50px"></div>
</#if>
    <a href="/">Homepage</a>
    <a href="/dashboard/">Dashboard</a>
    <a href="/network/comment/">Redner/Kommentatoren-Netzwerk</a>
    <a href="/network/speech/">Redner/Themen-Netzwerk</a>
    <a href="/network/topic/">Redner/Sentiment/Themen-Netzwerk</a>
    <a href="/reden/">Reden-Visualisierung</a>
    <a href="/protokolleditor/">Protokoll-Editor</a>
    <a href="/latex/">PDF-Export</a>
    <a href="/loginSite/">Login-Management</a>
</div>
<script>
/**
 * Shows the status bar.
 * @author Eric Lakhter
 */
function showStatusBar() {
    document.getElementById("parliament-browser-main-navigation-bar-show").style.display = 'flex';
}

/**
 * Hides the status bar.
 * @author Eric Lakhter
 */
function hideStatusBar() {
    document.getElementById("parliament-browser-main-navigation-bar-show").style.display = 'none';
}
</script>

<!-- end navigation bar -->
