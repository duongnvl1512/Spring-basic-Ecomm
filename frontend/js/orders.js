const API_URL = "http://localhost:8080/api/orders";

const ordersContainer = document.getElementById("ordersContainer");

async function loadOrders() {

    const response = await fetch(API_URL);

    const orders = await response.json();

    ordersContainer.innerHTML = "";

    orders.forEach(order => {

        let itemsHtml = "";

        order.orderItems.forEach(item => {

            itemsHtml += `
                <li>
                    ${item.product.name}
                    x ${item.quantity}
                </li>
            `;
        });

        ordersContainer.innerHTML += `

            <div class="card mb-4 p-3">

                <div class="d-flex justify-content-between">

                    <div>

                        <h5>
                            Order #${order.id}
                        </h5>

                        <p>
                            Customer:
                            ${order.customerName}
                        </p>

                        <p>
                            Phone:
                            ${order.customerPhone}
                        </p>

                    </div>

                    <div>

                        <span class="badge bg-primary">
                            ${order.status}
                        </span>

                    </div>

                </div>

                <hr>

                <h6>Products</h6>

                <ul>
                    ${itemsHtml}
                </ul>

                <hr>

                <strong>
                    Total:
                    $${order.totalPrice}
                </strong>

            </div>
        `;
    });
}

loadOrders();