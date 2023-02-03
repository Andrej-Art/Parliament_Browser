<!-- to be inserted at the beginning of every page -->

<div id="parliament-browser-main-navigation-bar">
    <button onclick="showStatusBar()">button hide</button>
</div>

<script>
    function showStatusBar() {
        let statusBar = document.getElementById("parliament-browser-main-navigation-bar");
        statusBar.style.width = '10%';
        statusBar.innerHTML = '<button onclick="hideStatusBar()">button show</button>';
    }
    function hideStatusBar() {
        let statusBar = document.getElementById("parliament-browser-main-navigation-bar");
        statusBar.style.width = '100%';
        statusBar.innerHTML = '<button onclick="showStatusBar()">button hide</button>';
    }
</script>
