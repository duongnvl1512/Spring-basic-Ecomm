async function loadComponent(id, file) {

    const response = await fetch(file);

    const html = await response.text();

    document.getElementById(id).innerHTML = html;
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