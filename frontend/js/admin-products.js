const API_URL = "http://localhost:8080/api/products";

const productTableBody = document.getElementById("productTableBody");
const productForm = document.getElementById("productForm");
const editProductForm = document.getElementById("editProductForm");

let products = [];
let allProducts = [];
let editProductId = null;

// Sort configuration
let currentSort = {
    field: null,
    direction: "asc"
};

// Load products
async function loadProducts() {
    try {
        const response = await fetch(API_URL);
        products = await response.json();
        allProducts = [...products];
        renderProductsTable(products);
    } catch (error) {
        console.error("Error loading products:", error);
    }
}

// Render Products Table
function renderProductsTable(products) {
    productTableBody.innerHTML = "";

    products.forEach(product => {
        productTableBody.innerHTML += `
            <tr>
                <td class="fw-bold">#${product.id}</td>
                <td class="fw-semibold text-dark">${product.name}</td>
                <td class="text-primary fw-medium">$${Number(product.price).toFixed(2)}</td>
                <td>
                    <span class="badge ${product.stockQuantity > 0 ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'} rounded-pill px-2.5 py-1">
                        ${product.stockQuantity} pcs
                    </span>
                </td>
                <td class="description-column text-muted small">${product.description || 'No description'}</td>
                <td class="text-end text-nowrap">
                    <button 
                        class="btn btn-outline-warning btn-sm me-1 d-inline-flex align-items-center gap-1"
                        onclick="editProduct(${product.id})"
                    >
                        <i class="bi bi-pencil-square"></i> Edit
                    </button>
                    <button 
                        class="btn btn-outline-danger btn-sm d-inline-flex align-items-center gap-1"
                        onclick="deleteProduct(${product.id})"
                    >
                        <i class="bi bi-trash"></i> Delete
                    </button>
                </td>
            </tr>
        `;
    });
}

// Add product
async function addProduct() {
    const product = {
        name: document.getElementById("name").value,
        description: document.getElementById("description").value,
        price: Number(document.getElementById("price").value),
        stockQuantity: Number(document.getElementById("stockQuantity").value)
    };

    if (!product.name || !product.price) {
        showToast("Please fill in Name and Price!", "danger");
        return;
    }

    try {
        const response = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(product)
        });

        if (!response.ok) throw new Error("Add product failed");

        showToast("Product added successfully!", "success");
        clearAddProductForm();
        
        // Tự đóng form sau khi lưu thành công (Nếu muốn)
        toggleAddProductForm(); 
        
        loadProducts();
    } catch (error) {
        showToast("Failed to add product!", "danger");
    }
}

// Helper clear form
function clearAddProductForm() {
    document.getElementById("name").value = "";
    document.getElementById("description").value = "";
    document.getElementById("price").value = "";
    document.getElementById("stockQuantity").value = "";
}

// Update product listener
editProductForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const product = {
        name: document.getElementById("editName").value,
        description: document.getElementById("editDescription").value,
        price: Number(document.getElementById("editPrice").value),
        stockQuantity: Number(document.getElementById("editStockQuantity").value)
    };

    try {
        const response = await fetch(`${API_URL}/${editProductId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(product)
        });

        if (!response.ok) throw new Error("Update failed");

        const modalElement = document.getElementById("editModal");
        const modal = bootstrap.Modal.getInstance(modalElement);
        modal.hide();

        showToast("Product updated successfully!", "success");
        loadProducts();
    } catch (error) {
        showToast("Failed to update product!", "danger");
    }
});

// Toggle Add Form Button
function toggleAddProductForm() {
    const formContainer = document.getElementById("addProductFormContainer");
    const toggleBtn = document.getElementById("toggleAddFormBtn");

    formContainer.classList.toggle("show");
    const isOpen = formContainer.classList.contains("show");
    
    // Đã sửa: Chuyển đổi icon FontAwesome sang Bootstrap Icon đồng bộ
    if (isOpen) {
        toggleBtn.innerHTML = `
            <i class="bi bi-x-lg"></i>
            Close Form
        `;
        toggleBtn.classList.replace("btn-primary", "btn-danger");
    } else {
        toggleBtn.innerHTML = `
            <i class="bi bi-plus-lg"></i>
            Add New Product
        `;
        toggleBtn.classList.replace("btn-danger", "btn-primary");
    }
}

// Open Delete Modal Confirmation
let deleteProductId = null;
function deleteProduct(id) {
    deleteProductId = id;
    const modal = new bootstrap.Modal(document.getElementById("deleteModal"));
    modal.show();
}

// Confirm Delete Execution
async function confirmDeleteProduct() {
    try {
        const response = await fetch(`${API_URL}/${deleteProductId}`, {
            method: "DELETE"
        });

        if (!response.ok) throw new Error("Delete failed");

        const modalElement = document.getElementById("deleteModal");
        bootstrap.Modal.getInstance(modalElement).hide();

        showToast("Product deleted successfully!", "success");
        loadProducts();
    } catch (error) {
        showToast("Failed to delete product!", "danger");
    }
}

// Open and populate Edit Modal
async function editProduct(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`);
        const product = await response.json();

        document.getElementById("editName").value = product.name;
        document.getElementById("editDescription").value = product.description;
        document.getElementById("editPrice").value = product.price;
        document.getElementById("editStockQuantity").value = product.stockQuantity;

        editProductId = id;

        const modal = new bootstrap.Modal(document.getElementById("editModal"));
        modal.show();
    } catch (error) {
        console.error("Error fetching product details:", error);
    }
}

// Sort features
function sortProducts(field) {
    if (currentSort.field === field) {
        currentSort.direction = currentSort.direction === "asc" ? "desc" : "asc";
    } else {
        currentSort.field = field;
        currentSort.direction = "asc";
    }

    products.sort((a, b) => {
        let valueA = a[field];
        let valueB = b[field];

        if (typeof valueA === "string") {
            valueA = valueA.toLowerCase();
            valueB = valueB.toLowerCase();

            if (valueA < valueB) return currentSort.direction === "asc" ? -1 : 1;
            if (valueA > valueB) return currentSort.direction === "asc" ? 1 : -1;
            return 0;
        }
        
        return currentSort.direction === "asc" ? valueA - valueB : valueB - valueA;
    });

    renderProductsTable(products);
}

// Client-side search filters
function searchProducts() {
    const keyword = document.getElementById("searchInput").value.toLowerCase();

    products = allProducts.filter(product =>
        (product.name && product.name.toLowerCase().includes(keyword)) ||
        (product.description && product.description.toLowerCase().includes(keyword))
    );

    renderProductsTable(products);
}

loadProducts();