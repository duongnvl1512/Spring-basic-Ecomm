async function loadComponent(id, file) {

    const response = await fetch(file);

    const html = await response.text();

    document.getElementById(id).innerHTML = html;
}

function toggleSidebar() {
    const sidebar = document.getElementById("sidebar");
    const toggleBtn = document.getElementById("toggleSidebarBtn");
    
    sidebar.classList.toggle("collapsed");
    
    const icon = toggleBtn.querySelector("i");
    if (sidebar.classList.contains("collapsed")) {
        icon.className = "bi bi-chevron-right fs-5";
    } else {
        icon.className = "bi bi-chevron-left fs-5";
    }
}

loadComponent(
    "sidebar-container",
    "./components/sidebar.html"
);

loadComponent(
    "navbar-container",
    "./components/navbar.html"
);

loadComponent(
    "toast-container",
    "./components/toast.html"
);