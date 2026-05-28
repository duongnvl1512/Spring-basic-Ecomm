const PRODUCTS_API =
    "http://localhost:8080/api/products";

const ORDERS_API =
    "http://localhost:8080/api/orders";

const productsContainer =
    document.getElementById("productsContainer");

let products = [];

let allProducts = [];

let cart = [];

async function loadProducts() {

    const response = await fetch(PRODUCTS_API);

    const products = await response.json();

    allProducts = products;

    renderProducts(products);
}

function renderProducts(products) {

    const container =
        document.getElementById("productsContainer");

    let html = "";

    products.forEach(product => {

        html += `

        <div class="col-md-6">

            <div class="card h-100 product-card">

                <div class="card-body">

                    <h5 class="card-title">
                        ${product.name}
                    </h5>

                    <p class="card-text">
                        ${product.description}
                    </p>

                    <p>

                        <strong>
                            $${product.price}
                        </strong>

                    </p>

                    <p>
                        Stock:
                        ${product.stockQuantity}
                    </p>

                    <button
                        class="btn btn-primary w-100"
                        onclick='addToCart(${JSON.stringify(product)})'>

                        <i class="fa-solid fa-cart-plus"></i>

                        Add To Cart

                    </button>

                </div>

            </div>

        </div>
        `;
    });

    container.innerHTML = html;
}

function addToCart(product) {

    const existingItem = cart.find(
        item => item.id === product.id
    );

    if (existingItem) {

        existingItem.quantity++;

    } else {

        cart.push({

            ...product,

            quantity: 1
        });
    }

    renderCart();

    showToast(
        "Added to cart!",
        "success"
    );
}

function renderCart() {

    const container =
        document.getElementById("cartContainer");

    if (cart.length === 0) {

        container.innerHTML = `
        
        <p class="text-muted">
            Cart is empty
        </p>
        `;

        return;
    }

    let html = "";

    let total = 0;

    cart.forEach(item => {

        total += item.price * item.quantity;

        html += `

        <div class="cart-item">

            <div class="d-flex justify-content-between">

                <strong>
                    ${item.name}
                </strong>

                <button
                    class="btn btn-sm btn-danger"
                    onclick="removeFromCart(${item.id})">

                    <i class="fa-solid fa-trash"></i>

                </button>

            </div>

            <div class="mt-2">

                <button
                    class="btn btn-sm btn-secondary"
                    onclick="decreaseQty(${item.id})">

                    -

                </button>

                <span class="mx-2">

                    ${item.quantity}

                </span>

                <button
                    class="btn btn-sm btn-secondary"
                    onclick="increaseQty(${item.id})">

                    +

                </button>

            </div>

            <p class="mt-2">

                $${item.price * item.quantity}

            </p>

        </div>
        `;
    });

    html += `

    <h4 class="mt-4">

        Total: $${total}

    </h4>
    `;

    container.innerHTML = html;
}

function increaseQty(id) {

    const item = cart.find(
        item => item.id === id
    );

    item.quantity++;

    renderCart();
}

function decreaseQty(id) {

    const item = cart.find(
        item => item.id === id
    );

    item.quantity--;

    if (item.quantity <= 0) {

        cart = cart.filter(
            item => item.id !== id
        );
    }

    renderCart();
}

function removeFromCart(id) {

    cart = cart.filter(
        item => item.id !== id
    );

    renderCart();
}

async function placeOrder() {

    if (cart.length === 0) {

        showToast(
            "Cart is empty!",
            "danger"
        );

        return;
    }

    const order = {

        customerName:
            document.getElementById("customerName").value,

        customerPhone:
            document.getElementById("customerPhone").value,

        items: cart.map(item => ({

            productId: item.id,

            quantity: item.quantity
        }))
    };

    try {

        const response = await fetch(
            "http://localhost:8080/api/orders",
            {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(order)
            }
        );

        if (!response.ok) {

            throw new Error();
        }

        showToast(
            "Order placed successfully!",
            "success"
        );

        cart = [];

        renderCart();

    } catch (error) {

        showToast(
            "Failed to place order!",
            "danger"
        );
    }
}

function searchProducts() {

    const keyword =
        document.getElementById("searchInput")
        .value
        .toLowerCase();

    const filtered = allProducts.filter(product =>

        product.name
            .toLowerCase()
            .includes(keyword)
    );

    renderProducts(filtered);
}

loadProducts();

