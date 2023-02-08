<!-- Parliament Browser navigation bar -->

<div style="position: absolute;">
    <button class="nav-button" onclick="showStatusBar()"><i class='fa fa-angle-double-down'></i></button>
</div>
<div id="parliament-browser-main-navigation-bar">
    <button class="nav-button" onclick="hideStatusBar()"><i class='fa fa-angle-double-up'></i></button>
    <a href="/">Homepage</a>
    <a href="/dashboard/">Dashboard</a>
    <a href="/network/1/">Redner-Kategorien-Netzwerk</a>
    <a href="/reden/">Reden-Visualisierung</a>
    <a href="/protokolleditor/">Protokoll-Editor</a>
    <a href="/latex/">LaTeX-Editor</a>
    <a href="/loginSite/">Login-Management</a>
</div>
<script>
/**
 * Shows the status bar.
 * @author Eric Lakhter
 */
function showStatusBar() {
    document.getElementById("parliament-browser-main-navigation-bar").style.display = 'flex';
}

/**
 * Hides the status bar.
 * @author Eric Lakhter
 */
function hideStatusBar() {
    document.getElementById("parliament-browser-main-navigation-bar").style.display = 'none';
}
</script>

<!-- end navigation bar -->
