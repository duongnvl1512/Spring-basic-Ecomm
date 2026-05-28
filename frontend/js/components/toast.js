function showToast(message, type = "success") {

    const toastElement =
        document.getElementById("appToast");

    const toastMessage =
        document.getElementById("toastMessage");

    let icon = "bi-check-circle-fill";

    if (type === "danger") {

        icon = "bi-x-circle-fill";
    }

    if (type === "warning") {

        icon = "bi-exclamation-triangle-fill";
    }

    toastMessage.innerHTML = `
        <i class="bi ${icon} me-2"></i>
        ${message}
    `;

    toastElement.className =
        `toast align-items-center text-bg-${type} border-0`;

    const toast =
        new bootstrap.Toast(toastElement);

    toast.show();
}