const API_URL = "http://localhost:8080/api/products";

const productTableBody = document.getElementById("productTableBody");
const productForm = document.getElementById("productForm");
const editProductForm = document.getElementById("editProductForm");

let editProductId = null;


// Load products
async function loadProducts() {

    const response = await fetch(API_URL);

    const products = await response.json();

    productTableBody.innerHTML = "";

    products.forEach(product => {

        productTableBody.innerHTML += `
            <tr>
                <td>${product.id}</td>
                <td>${product.name}</td>
                <td>${product.price}</td>
                <td>${product.stockQuantity}</td>
                <td>
                    <button 
                        class="btn btn-warning btn-sm"
                        onclick="editProduct(${product.id})"
                    >
                    <i class="bi bi-pencil-square"></i>
                        Edit
                    </button>

                    <button 
                        class="btn btn-danger btn-sm"
                        onclick="deleteProduct(${product.id})"
                    >
                    <i class="bi bi-trash"></i>
                        Delete
                    </button>
                </td>
            </tr>
        `;
    });
}

async function addProduct() {

    const product = {

        name: document.getElementById("name").value,

        description: document.getElementById("description").value,

        price: document.getElementById("price").value,

        stockQuantity: document.getElementById("stockQuantity").value
    };

    try {

        const response = await fetch(API_URL, {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(product)
        });

        if (!response.ok) {

            throw new Error("Add product failed");
        }

        showToast(
            "Product added successfully!",
            "success"
        );

        loadProducts();

    } catch (error) {

        showToast(
            "Failed to add product!",
            "danger"
        );
    }
}

// Create product
editProductForm.addEventListener("submit", async (e) => {

    e.preventDefault();

    const product = {

        name: document.getElementById("editName").value,

        description: document.getElementById("editDescription").value,

        price: Number(document.getElementById("editPrice").value),

        stockQuantity: Number(document.getElementById("editStockQuantity").value)
    };

    await fetch(`${API_URL}/${editProductId}`, {

        method: "PUT",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify(product)
    });

    const modal = bootstrap.Modal.getInstance(
        document.getElementById("editModal")
    );

    modal.hide();

    loadProducts();
});

function toggleAddProductForm() {
    const formContainer = document.getElementById(
        "addProductFormContainer"
    );

    const toggleBtn = document.getElementById(
        "toggleAddFormBtn"
    );

    formContainer.classList.toggle("show");

    const isOpen = formContainer.classList.contains("show");
    
    if (isOpen) {
        toggleBtn.innerHTML = `
            <i class="fa-solid fa-minus"></i>
            Close Form
        `;

        toggleBtn.classList.remove("btn-primary");
        toggleBtn.classList.add("btn-danger");
    } else {
        toggleBtn.innerHTML = `
            <i class="fa-solid fa-plus"></i>
            Add New Product
        `;

        toggleBtn.classList.remove("btn-danger");
        toggleBtn.classList.add("btn-primary");
    }
}

// Delete product
let deleteProductId = null;
function deleteProduct(id) {

    deleteProductId = id;

    const modal = new bootstrap.Modal(
        document.getElementById("deleteModal")
    );

    modal.show();
}

async function confirmDeleteProduct() {

    try {

        const response = await fetch(
            `${API_URL}/${deleteProductId}`,
            {
                method: "DELETE"
            }
        );

        if (!response.ok) {

            throw new Error("Delete failed");
        }

        bootstrap.Modal
            .getInstance(
                document.getElementById("deleteModal")
            )
            .hide();

        showToast(
            "Product deleted successfully!",
            "success"
        );

        loadProducts();

    } catch (error) {

        showToast(
            "Failed to delete product!",
            "danger"
        );
    }
}

//edit function
async function editProduct(id) {

    const response = await fetch(`${API_URL}/${id}`);

    const product = await response.json();

    document.getElementById("editName").value = product.name;

    document.getElementById("editDescription").value = product.description;

    document.getElementById("editPrice").value = product.price;

    document.getElementById("editStockQuantity").value = product.stockQuantity;

    editProductId = id;

    const modal = new bootstrap.Modal(
        document.getElementById("editModal")
    );

    modal.show();
}

loadProducts();