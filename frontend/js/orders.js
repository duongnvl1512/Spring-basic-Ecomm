const API_URL = "http://localhost:8080/api/orders";

const ordersContainer = document.getElementById("ordersContainer");

let orders = [];
let allOrders = [];

async function loadOrders() {

    const response = await fetch(API_URL);
    orders = await response.json();

    allOrders = [...orders];
    renderOrdersTable(orders);
}

function renderOrdersTable(orders) {

    const container = document.getElementById("ordersContainer");

    if (orders.length === 0) {
        container.innerHTML = `
            <div class="alert alert-warning">
                No orders found
            </div>
        `;
        return;
    }

    let html = `
        <table class="table table-bordered bg-white">
            <thead class="table-white">
                <tr>
                    <th>ID</th>
                    <th>Customer</th>
                    <th>Phone</th>
                    <th>Total</th>
                    <th>Status</th>
                    <th>Action</th>
                    <th>Created At</th>
                </tr>
            </thead>
            <tbody>
    `;

    orders.forEach(order => {

        html += `
            <tr>
                <td>
                    #${order.id}
                </td>
                <td>
                    ${order.customerName}
                </td>
                <td>
                    ${order.customerPhone}
                </td>
                <td>
                    $${order.totalPrice}
                </td>
                <td>
                    <span class="badge bg-primary">
                        ${order.status}
                    </span>
                </td>
                <td>
                    <button class="btn btn-sm btn-info" onclick="viewOrderDetail(${order.id})">
                        View
                    </button>
                </td>
                <td>
                    ${new Date(order.createdAt).toLocaleString()}
                </td>
            </tr>
        `;
    });

    html += `
            </tbody>
        </table>
    `;

    container.innerHTML = html;
}

function searchOrders() {
    const keyword =
        document.getElementById("searchOrderInput")
        .value
        .toLowerCase();

    orders = allOrders.filter(order =>
        order.customerName
            .toLowerCase()
            .includes(keyword)
        ||
        order.customerPhone
            .toLowerCase()
            .includes(keyword)
        ||
        order.id
            .toString()
            .includes(keyword)
    );

    renderOrdersTable(orders);
}

async function viewOrderDetail(orderId) {
    try {
        const res = await fetch(`http://localhost:8080/api/orders/${orderId}`);
        const order = await res.json();

        renderOrderDetail(order);

        const modal = new bootstrap.Modal(
            document.getElementById("orderDetailModal")
        );

        modal.show();

    } catch (error) {
        console.error(error);
        showToast("Failed to load order detail", "danger");
    }
}

function renderOrderDetail(order) {

    let html = `
        <div class="mb-3">
            <strong>Customer:</strong> ${order.customerName}<br>
            <strong>Phone:</strong> ${order.customerPhone}<br>
            <strong>Total:</strong> $${order.totalPrice}
        </div>
        <hr/>

        <div class="mb-3">
            <label class="form-label">Status</label>

            <select id="statusSelect" class="form-select">
                <option value="PENDING">PENDING</option>
                <option value="APPROVED">APPROVED</option>
                <option value="DELIVERED">DELIVERED</option>
                <option value="CANCELLED">CANCELLED</option>
            </select>
        </div>

        <button class="btn btn-success mb-3" onclick="updateOrderStatus(${order.id})">
            Save Status
        </button>

        <h6>Order List</h6>
        <table class="table table-sm table-bordered">
            <thead>
                <tr>
                    <th>Product</th>
                    <th>Quantity</th>
                    <th>Price</th>
                </tr>
            </thead>
            <tbody>

    `;

    order.items.forEach(item => {
        html += `
            <tr>
                <td>${item.productName}</td>
                <td>${item.quantity}</td>
                <td>$${item.price}</td>
            </tr>
        `;
    });
    html += `
            </tbody>
        </table>
    `;
    document.getElementById("orderDetailContent").innerHTML = html;
}

function renderStatus(status) {
    const map = {
        PENDING: "bg-warning",
        CONFIRMED: "bg-primary",
        SHIPPING: "bg-info",
        COMPLETED: "bg-success",
        CANCELLED: "bg-danger"
    };
    return `
        <span class="badge ${map[status] || "bg-secondary"}">
            ${status}
        </span>
    `;
}

async function updateOrderStatus(orderId) {
    const status = document.getElementById("statusSelect").value;

    try {
        const res = await fetch(
            `http://localhost:8080/api/orders/${orderId}/status?status=${status}`,
            {
                method: "PUT"
            }
        );

        if (!res.ok) {
            throw new Error("Update failed");
        }

        const data = await res.json();

        renderOrderDetail(data);
        loadOrders();
        showToast("Update status successfully!", "success");

    } catch (error) {
        console.error(error);
        showToast("Update status failed!", "danger");
    }
}


loadOrders();