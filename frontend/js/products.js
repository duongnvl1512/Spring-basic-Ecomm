const PRODUCTS_API = "http://localhost:8080/api/products";
const ORDERS_API = "http://localhost:8080/api/orders";

const productsContainer = document.getElementById("productsContainer");

let products = [];
let allProducts = [];
let cart = [];

// Load products
async function loadProducts() {
    try {
        const response = await fetch(PRODUCTS_API);
        const data = await response.json();
        allProducts = data;
        renderProducts(data);
    } catch (error) {
        console.error("Error loading products:", error);
    }
}

// Render Products Grid
function renderProducts(products) {
    const container = document.getElementById("productsContainer");
    let html = "";

    products.forEach(product => {
        const isOutOfStock = product.stockQuantity <= 0;
        
        html += `
        <div class="col-md-6 col-lg-4 mb-4">
            <div class="card h-100 border-0 shadow-sm product-card rounded-3 overflow-hidden">
                <div class="card-body d-flex flex-column p-4">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <span class="badge ${isOutOfStock ? 'bg-danger-subtle text-danger' : 'bg-success-subtle text-success'} rounded-pill px-2.5 py-1 small">
                            ${isOutOfStock ? 'Out of Stock' : `In Stock: ${product.stockQuantity}`}
                        </span>
                    </div>

                    <h5 class="card-title fw-bold text-dark mb-2">${product.name}</h5>
                    
                    <p class="card-text text-muted small flex-grow-1 mb-3">
                        ${product.description || 'No description available for this item.'}
                    </p>

                    <div class="pt-3 border-top mt-auto">
                        <div class="d-flex justify-content-between align-items-baseline mb-3">
                            <span class="text-muted small">Price</span>
                            <span class="fs-4 fw-bold text-primary">$${Number(product.price).toFixed(2)}</span>
                        </div>
                        
                        <button
                            class="btn ${isOutOfStock ? 'btn-secondary' : 'btn-primary'} w-100 d-flex align-items-center justify-content-center gap-2 fw-semibold shadow-sm py-2"
                            ${isOutOfStock ? 'disabled' : ''}
                            onclick='addToCart(${JSON.stringify(product)})'>
                            <i class="bi bi-cart-plus fs-5"></i>
                            <span>${isOutOfStock ? 'Sold Out' : 'Add To Cart'}</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
        `;
    });

    container.innerHTML = html || `<div class="col-12 text-center text-muted py-5">No products found.</div>`;
}

// Add to cart
function addToCart(product) {
    const existingItem = cart.find(item => item.id === product.id);

    if (existingItem) {
        existingItem.quantity++;
    } else {
        cart.push({
            ...product,
            quantity: 1
        });
    }

    renderCart();
    showToast(`Added "${product.name}" to cart!`, "success");
}

// Render Cart UI
function renderCart() {
    const container = document.getElementById("cartContainer");

    if (cart.length === 0) {
        container.innerHTML = `
            <div class="text-center py-4 text-muted">
                <i class="bi bi-cart-x display-6 d-block mb-2 text-secondary"></i>
                <p class="mb-0 small">Your cart is empty</p>
            </div>
        `;
        return;
    }

    let html = "";
    let total = 0;

    cart.forEach(item => {
        const itemTotal = item.price * item.quantity;
        total += itemTotal;

        html += `
        <div class="cart-item py-3 border-bottom">
            <div class="d-flex justify-content-between align-items-start gap-2">
                <div>
                    <h6 class="fw-bold text-dark mb-1 small">${item.name}</h6>
                    <span class="text-muted small">$${Number(item.price).toFixed(2)} / unit</span>
                </div>
                <button
                    class="btn btn-sm btn-link text-danger p-0 border-0 text-decoration-none"
                    onclick="removeFromCart(${item.id})"
                    title="Remove item"
                >
                    <i class="bi bi-trash3"></i>
                </button>
            </div>

            <div class="d-flex justify-content-between align-items-center mt-3">
                <div class="input-group input-group-sm" style="width: 100px;">
                    <button class="btn btn-outline-secondary px-2 py-0" type="button" onclick="decreaseQty(${item.id})">-</button>
                    <span class="form-control text-center bg-light px-1 py-0 fw-semibold">${item.quantity}</span>
                    <button class="btn btn-outline-secondary px-2 py-0" type="button" onclick="increaseQty(${item.id})">+</button>
                </div>
                
                <span class="fw-bold text-dark small">$${itemTotal.toFixed(2)}</span>
            </div>
        </div>
        `;
    });

    // Thẻ hiển thị tổng giá trị giỏ hàng cao cấp
    html += `
    <div class="mt-4 p-3 bg-light rounded-3">
        <div class="d-flex justify-content-between align-items-center">
            <span class="fw-semibold text-secondary">Grand Total:</span>
            <span class="h4 fw-bold text-success mb-0">$${total.toFixed(2)}</span>
        </div>
    </div>
    `;

    container.innerHTML = html;
}

// Increase Qty
function increaseQty(id) {
    const item = cart.find(item => item.id === id);
    if (item) {
        item.quantity++;
        renderCart();
    }
}

// Decrease Qty
function decreaseQty(id) {
    const item = cart.find(item => item.id === id);
    if (!item) return;

    item.quantity--;

    if (item.quantity <= 0) {
        cart = cart.filter(item => item.id !== id);
    }

    renderCart();
}

// Remove from cart
function removeFromCart(id) {
    cart = cart.filter(item => item.id !== id);
    renderCart();
}

// Place Order Action
async function placeOrder() {
    if (cart.length === 0) {
        showToast("Cart is empty!", "danger");
        return;
    }

    const customerNameInput = document.getElementById("customerName");
    const customerPhoneInput = document.getElementById("customerPhone");

    const order = {
        customerName: customerNameInput.value.trim(),
        customerPhone: customerPhoneInput.value.trim(),
        items: cart.map(item => ({
            productId: item.id,
            quantity: item.quantity
        }))
    };

    // Kiểm tra tính hợp lệ của Form thông tin khách hàng trước khi gửi
    if (!order.customerName || !order.customerPhone) {
        showToast("Please enter customer details!", "danger");
        return;
    }

    try {
        const response = await fetch(ORDERS_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(order)
        });

        if (!response.ok) throw new Error();

        showToast("Order placed successfully!", "success");
        
        // Reset form thông tin và giỏ hàng
        customerNameInput.value = "";
        customerPhoneInput.value = "";
        cart = [];
        renderCart();

    } catch (error) {
        showToast("Failed to place order!", "danger");
    }
}

// Search Product client filter
function searchProducts() {
    const keyword = document.getElementById("searchInput").value.toLowerCase();

    const filtered = allProducts.filter(product =>
        product.name && product.name.toLowerCase().includes(keyword)
    );

    renderProducts(filtered);
}

// Initial Launch
loadProducts();