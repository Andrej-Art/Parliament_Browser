<!-- to be inserted at the beginning of every page -->

<div id="navigation-bar">
    <button onclick="showStatusBar()">button hide</button>
</div>

<script>
    function showStatusBar() {
        let statusBar = document.getElementById("navigation-bar");
        statusBar.style.width = '10%';
        statusBar.innerHTML = '<button onclick="hideStatusBar()">button show</button>';
    }
    function hideStatusBar() {
        let statusBar = document.getElementById("navigation-bar");
        statusBar.style.width = '100%';
        statusBar.innerHTML = '<button onclick="showStatusBar()">button hide</button>';
    }
</script>
