const API_URL = "http://localhost:8080/api/orders";

const ordersContainer = document.getElementById("ordersContainer");

let orders = [];
let allOrders = [];

// Load orders
async function loadOrders() {
    try {
        const response = await fetch(API_URL);
        orders = await response.json();
        allOrders = [...orders];
        renderOrdersTable(orders);
        
        // Cập nhật số lượng đơn hàng lên badge tiêu đề (nếu có thẻ này trên HTML)
        const countBadge = document.querySelector(".dynamic-order-count");
        if (countBadge) countBadge.innerText = `${orders.length} orders`;
    } catch (error) {
        console.error("Error loading orders:", error);
    }
}

// Render Orders Table
function renderOrdersTable(orders) {
    const container = document.getElementById("ordersContainer");

    if (orders.length === 0) {
        container.innerHTML = `
            <div class="alert alert-warning border-0 shadow-sm d-flex align-items-center gap-2" role="alert">
                <i class="bi bi-exclamation-triangle-fill fs-5"></i>
                <div>No orders found matching your criteria.</div>
            </div>
        `;
        return;
    }

    // Bọc bảng trong lớp table-responsive để chống tràn màn hình điện thoại
    let html = `
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light text-secondary">
                    <tr>
                        <th style="width: 100px;">Order ID</th>
                        <th>Customer</th>
                        <th>Phone Number</th>
                        <th>Total Price</th>
                        <th>Status</th>
                        <th>Created Date</th>
                        <th class="text-end" style="width: 120px;">Action</th>
                    </tr>
                </thead>
                <tbody>
    `;

    orders.forEach(order => {
        html += `
            <tr>
                <td class="fw-bold text-dark">#ORD-${order.id}</td>
                <td class="fw-semibold">${order.customerName}</td>
                <td class="text-muted small">${order.customerPhone || 'N/A'}</td>
                <td class="fw-bold text-primary">$${Number(order.totalPrice).toFixed(2)}</td>
                <td>
                    ${renderStatus(order.status)}
                </td>
                <td class="text-muted small">
                    ${new Date(order.createdAt).toLocaleString()}
                </td>
                <td class="text-end">
                    <button class="btn btn-sm btn-outline-primary d-inline-flex align-items-center gap-1" onclick="viewOrderDetail(${order.id})">
                        <i class="bi bi-eye"></i> View Detail
                    </button>
                </td>
            </tr>
        `;
    });

    html += `
                </tbody>
            </table>
        </div>
    `;

    container.innerHTML = html;
}

// Client-side search filters
function searchOrders() {
    const keyword = document.getElementById("searchOrderInput").value.toLowerCase();

    orders = allOrders.filter(order =>
        (order.customerName && order.customerName.toLowerCase().includes(keyword)) ||
        (order.customerPhone && order.customerPhone.toLowerCase().includes(keyword)) ||
        (order.id && order.id.toString().includes(keyword))
    );

    renderOrdersTable(orders);
}

// Open Order Detail Modal
async function viewOrderDetail(orderId) {
    try {
        const res = await fetch(`http://localhost:8080/api/orders/${orderId}`);
        const order = await res.json();

        renderOrderDetail(order);

        const modal = new bootstrap.Modal(document.getElementById("orderDetailModal"));
        modal.show();

    } catch (error) {
        console.error(error);
        showToast("Failed to load order detail", "danger");
    }
}

// Render Content Inside Order Detail Modal
function renderOrderDetail(order) {
    let html = `
        <div class="row g-3 mb-4 bg-light p-3 rounded-3 border mx-0">
            <div class="col-sm-6">
                <span class="text-uppercase text-secondary fw-bold small d-block mb-1">Customer Details</span>
                <div class="fw-bold text-dark mb-1"><i class="bi bi-person me-2"></i>${order.customerName}</div>
                <div class="text-muted small"><i class="bi bi-telephone me-2"></i>${order.customerPhone || 'No phone'}</div>
            </div>
            <div class="col-sm-6 text-sm-end">
                <span class="text-uppercase text-secondary fw-bold small d-block mb-1">Invoice Payment</span>
                <div class="h5 fw-bold text-success mb-1">$${Number(order.totalPrice).toFixed(2)}</div>
                <div>${renderStatus(order.status)}</div>
            </div>
        </div>

        <div class="card border-warning-subtle bg-warning-subtle bg-opacity-10 mb-4">
            <div class="card-body py-3">
                <label class="form-label fw-bold text-dark small mb-2 text-uppercase">Update Order Status</label>
                <div class="input-group input-group-sm" style="max-width: 400px;">
                    <select id="statusSelect" class="form-select fw-semibold">
                        <option value="PENDING">PENDING</option>
                        <option value="APPROVED">APPROVED</option>
                        <option value="DELIVERED">DELIVERED</option>
                        <option value="CANCELLED">CANCELLED</option>
                    </select>
                    <button class="btn btn-success d-inline-flex align-items-center gap-1 px-3" onclick="updateOrderStatus(${order.id})">
                        <i class="bi bi-check-circle"></i> Save Status
                    </button>
                </div>
            </div>
        </div>

        <h6 class="fw-bold text-dark mb-2 text-uppercase small"><i class="bi bi-box-seam me-1"></i>Items List</h6>
        <div class="table-responsive border rounded-3">
            <table class="table table-sm table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-3 py-2">Product Name</th>
                        <th class="text-center py-2" style="width: 100px;">Quantity</th>
                        <th class="text-end pe-3 py-2" style="width: 120px;">Price</th>
                    </tr>
                </thead>
                <tbody>
    `;

    order.items.forEach(item => {
        html += `
            <tr>
                <td class="ps-3 fw-medium text-dark">${item.productName || 'Unknown Product'}</td>
                <td class="text-center fw-semibold text-secondary">${item.quantity}</td>
                <td class="text-end pe-3 text-primary fw-medium">$${Number(item.price).toFixed(2)}</td>
            </tr>
        `;
    });
    
    html += `
                </tbody>
            </table>
        </div>
    `;
    
    document.getElementById("orderDetailContent").innerHTML = html;

    // Tự động gán giá trị hiện tại của đơn hàng vào ô Select trạng thái
    const statusSelect = document.getElementById("statusSelect");
    if (statusSelect) {
        statusSelect.value = order.status;
    }
}

// Render Status Badges Helper (Đã đồng bộ giao diện bo tròn cao cấp)
function renderStatus(status) {
    const map = {
        PENDING: "bg-warning-subtle text-warning border border-warning-subtle",
        APPROVED: "bg-primary-subtle text-primary border border-primary-subtle",
        DELIVERED: "bg-success-subtle text-success border border-success-subtle",
        CANCELLED: "bg-danger-subtle text-danger border border-danger-subtle"
    };
    return `
        <span class="badge ${map[status] || "bg-secondary-subtle text-secondary"} rounded-pill px-2.5 py-1 fw-bold text-uppercase small">
            ${status}
        </span>
    `;
}

// Action Update Order Status
async function updateOrderStatus(orderId) {
    const status = document.getElementById("statusSelect").value;

    try {
        const res = await fetch(
            `http://localhost:8080/api/orders/${orderId}/status?status=${status}`,
            {
                method: "PUT"
            }
        );

        if (!res.ok) throw new Error("Update failed");

        const data = await res.json();

        renderOrderDetail(data);
        loadOrders(); // Tải lại bảng tổng quan ở nền ngoài
        showToast("Order status updated successfully!", "success");

    } catch (error) {
        console.error(error);
        showToast("Failed to update order status!", "danger");
    }
}

// Initial Launch
loadOrders();