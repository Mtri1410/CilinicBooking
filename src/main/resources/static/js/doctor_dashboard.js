$(document).ready(function () {
    showdt();
    $("#btnShow").on("click", function () {
        const type = $("#type").val();  // "Doanh thu" hoặc "Sản phẩm"
        const chartVisible = document.getElementById('chartSection').style.display !== 'none';  // true nếu đang hiện biểu đồ
        const tableVisible = document.getElementById('tableSection').style.display !== 'none'
            || document.getElementById('tableSection_revenue').style.display !== 'none';  // true nếu đang hiện bảng

        if (chartVisible) {
            // Ở chế độ biểu đồ
            if (type === "Doanh thu") {
                LoadData();
            } else if (type === "Sản phẩm") {
                LoadProductRevenueChart();
            }
        } else if (tableVisible) {
            // Ở chế độ bảng
            if (type === "Doanh thu") {
                // Gọi hàm load dữ liệu bảng doanh thu
                loadTableRevenueData();  // Bạn phải có hàm này để load dữ liệu bảng Doanh thu
            } else if (type === "Sản phẩm") {
                // Gọi hàm load dữ liệu bảng sản phẩm
                loadTableProductData();  // Bạn phải có hàm này để load dữ liệu bảng sản phẩm
            }
        }
    });
    // $('a[data-bs-toggle="tab"]').on("shown.bs.tab", function (e) {
    //     if ($(e.target).attr("href") === "#chart-tab") {
    //         LoadData();
    //     }
    // });
});
let revenueChart;

function LoadData() {
    const startDate = $("#startDate").val();
    const endDate = $("#endDate").val();

    if (!startDate || !endDate) {
        $("#chartSection").hide();
        return;
    }

    $.ajax({
        url: "/api/order/revunue/chart",
        method: "GET",
        data: {
            startDate: startDate + "T00:00:00",
            endDate: endDate + "T23:59:59"
        },
        success: function (data) {
            if (!data || data.length === 0) {
                $("#chartSection").hide();
                return;
            }

            const labels = [];
            const revenues = [];

            data.forEach(item => {
                const dateOnly = item.orderDate.split('T')[0];
                labels.push(dateOnly);
                revenues.push(item.totalRevenue);
            });

            // $("#chartSection").show();

            const ctx = document.getElementById("myChart").getContext("2d");

            if (window.myChart instanceof Chart) {
                window.myChart.destroy();
            }
            window.myChart = new Chart(ctx, {
                type: "line",
                data: {
                    labels: labels,
                    datasets: [{
                        label: "Doanh thu (VNĐ)",
                        data: revenues,
                        borderColor: "rgba(75, 192, 192, 1)",
                        backgroundColor: "rgba(75, 192, 192, 0.2)",
                        tension: 0.3,
                        fill: true,
                        pointRadius: 5,
                        pointHoverRadius: 7
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: "Ngày"  // hoặc "Tháng" tùy bạn muốn
                            }
                        },
                        y: {
                            title: {
                                display: true,
                                text: "Doanh thu (VNĐ)"
                            },
                            beginAtZero: true
                        }
                    }
                }
            });
        },
        error: function (err) {
            console.error("Lỗi khi tải biểu đồ doanh thu:", err);
            $("#revenueChart").hide();
        }
    });
}
function LoadProductRevenueChart() {
    const startDate = $("#startDate").val();
    const endDate = $("#endDate").val();

    if (!startDate || !endDate) {
        $("#chartSection").hide();
        return;
    }

    $.ajax({
        url: "/api/order/revunueproduct/chart",
        method: "GET",
        data: {
            startDate: startDate + "T00:00:00",
            endDate: endDate + "T23:59:59"
        },
        success: function (data) {
            if (!data || data.length === 0) {
                $("#chartSection").hide();
                return;
            }

            const labels = [];
            const revenues = [];

            data.forEach(item => {
                labels.push(item.productName);
                revenues.push(item.totalRevenue);
            });

            $("#chartSection").show();

            const ctx = document.getElementById("myChart").getContext("2d");

            if (window.myChart instanceof Chart) {
                window.myChart.destroy();
            }

            window.myChart = new Chart(ctx, {
                type: "bar",  // biểu đồ cột
                data: {
                    labels: labels,
                    datasets: [{
                        label: "Doanh thu (VNĐ)",
                        data: revenues,
                        backgroundColor: "rgba(75, 192, 192, 0.7)",
                        borderColor: "rgba(75, 192, 192, 1)",
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: "Sản phẩm"
                            }
                        },
                        y: {
                            title: {
                                display: true,
                                text: "Doanh thu (VNĐ)"
                            },
                            beginAtZero: true
                        }
                    }
                }
            });
        },
        error: function (err) {
            console.error("Lỗi khi tải biểu đồ doanh thu sản phẩm:", err);
            $("#chartSection").hide();
        }
    });
}
function loadTableRevenueData(page, pageSize, keyword) {
    page = page || 1;
    pageSize = pageSize || 10;
    keyword = keyword || '';

    let startDate = new Date($("#startDate").val()).toISOString().split(".")[0];
    let endDate = new Date($("#endDate").val()).toISOString().split(".")[0];

    $.ajax({
        url: `/api/order/revunue/table`,
        method: 'GET',
        data: {
            startDate: startDate,
            endDate: endDate,
            keyword: keyword,
            page: page,
            pagesize: pageSize
        },
        success: function (res) {
            $("#revenueTable tbody").empty();

            if (res.Data.length === 0) {
                $("#revenueTable tbody").append("<tr><td colspan='4' class='text-center'>Không có dữ liệu</td></tr>");
            } else {
                res.Data.forEach(function (item, index) {
                    const row = `
                        <tr>
                            <td>${item.patientName || ''}</td>
                            <td>${item.totalRevenue || 0}</td>
                            <td>${item.orderDate || ''}</td>

                        </tr>`;
                    $("#revenueTable tbody").append(row);
                });
            }

            renderRevenuePagination(page, res.NumberPage, pageSize, keyword);
        },
        error: function (err) {
            console.error("Lỗi khi load bảng doanh thu:", err);
            // Hiển thị chi tiết lỗi nếu có
            if (err.responseJSON) {
                console.error("Chi tiết lỗi JSON:", err.responseJSON);
            }
            if (err.responseText) {
                console.error("Chi tiết lỗi Text:", err.responseText);
                try {
                    // Thử phân tích nội dung lỗi
                    const errorObj = JSON.parse(err.responseText);
                    console.error("Đối tượng lỗi:", errorObj);
                } catch (e) {
                    console.error("Không thể phân tích phản hồi lỗi:", err.responseText);
                }
            }
            console.error("Status code:", err.status);
            console.error("Status text:", err.statusText);
        }
    });
}

function renderRevenuePagination(current, total, pageSize, keyword) {
    let paginationHTML = '';
    keyword = keyword || "";

    if (total <= 1) {
        $("#paginationRevenue").html('');
        return;
    }

    if (current > 1) {
        paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="loadTableRevenueData(${current - 1}, ${pageSize}, '${keyword}')">Previous</a></li>`;
    }

    for (let i = 1; i <= total; i++) {
        if (i === current) {
            paginationHTML += `<li class="page-item active"><span class="page-link">${i}</span></li>`;
        } else {
            paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="loadTableRevenueData(${i}, ${pageSize}, '${keyword}')">${i}</a></li>`;
        }
    }

    if (current < total) {
        paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="loadTableRevenueData(${current + 1}, ${pageSize}, '${keyword}')">Next</a></li>`;
    }

    $("#paginationRevenue").html(`<ul class="pagination">${paginationHTML}</ul>`);
}

function onSearchRevenue() {
    const keyword = $("#searchRevenue").val();
    loadTableRevenueData(1, 10, keyword);
}
function loadTableProductData(page, pagesize, keyword) {
    page = page || 1;
    pagesize = pagesize || 10;
    keyword = keyword || "";

    // Format ngày nếu bạn lấy từ input, ví dụ:
    let startDate = new Date($("#startDate").val()).toISOString();
    let endDate = new Date($("#endDate").val()).toISOString();

    $.ajax({
        url: `/api/order/revunueproduct/table`,
        method: 'GET',
        data: {
            startDate: startDate,
            endDate: endDate,
            keyword: keyword,
            page: page,
            pagesize: pagesize
        },
        success: function (res) {
            $("#productRevenueTable tbody").empty();

            res.Data.forEach(function (item) {
                var row = "<tr>" +
                    "<td>" + item.productName + "</td>" +
                    "<td>" + item.totalQuantity + "</td>" +
                    "<td>" + item.totalRevenue + "</td>" +
                    "<td>" + item.date + "</td>" +
                    "</tr>";
                $("#productRevenueTable tbody").append(row);
            });

            renderProductPagination(res.CurrentPage, res.NumberPage, pagesize, keyword);
        },
        error: function (err) {
            console.error("Lỗi khi load dữ liệu doanh thu sản phẩm:", err);
        }
    });
}

function renderProductPagination(current, total, size, keyword) {
    let paginationHTML = '';
    keyword = keyword || "";

    if (total <= 1) {
        $("#productPagination").html('');
        return;
    }

    if (current > 1) {
        paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="loadTableProductData(${current - 1}, ${size}, '${keyword}')">Previous</a></li>`;
    }

    for (let i = 1; i <= total; i++) {
        if (i === current) {
            paginationHTML += `<li class="page-item active"><span class="page-link">${i}</span></li>`;
        } else {
            paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="loadTableProductData(${i}, ${size}, '${keyword}')">${i}</a></li>`;
        }
    }

    if (current < total) {
        paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="loadTableProductData(${current + 1}, ${size}, '${keyword}')">Next</a></li>`;
    }

    $("#productPagination").html(`<ul class="pagination">${paginationHTML}</ul>`);
}

function onSearchProduct() {
    var keyword = $("#productSearchInput").val();
    loadTableProductData(1, 10, keyword);
}
function getRandomColor() {
    const r = Math.floor(Math.random() * 156 + 100); // tránh màu quá tối
    const g = Math.floor(Math.random() * 156 + 100);
    const b = Math.floor(Math.random() * 156 + 100);
    return `rgb(${r}, ${g}, ${b})`;
}
function showChart() {
    document.getElementById('chartSection').style.display = 'block';
    document.getElementById('tableSection').style.display = 'none';
    document.getElementById('tableSection_revenue').style.display = 'none';
    document.getElementById('btnChart').classList.remove('btn-outline-primary');
    document.getElementById('btnChart').classList.add('btn-primary');

    document.getElementById('btnTable').classList.remove('btn-secondary');
    document.getElementById('btnTable').classList.add('btn-outline-secondary');
}

function showTable() {

    document.getElementById('chartSection').style.display = 'none';
    const va = document.getElementById('type').value;
    if (va === 'Doanh thu') {
        // Hiển thị bảng doanh thu, ẩn bảng sản phẩm
        document.getElementById('tableSection_revenue').style.display = 'none';
        document.getElementById('tableSection').style.display = 'block';
    } else if (va === 'Sản phẩm') {
        // Hiển thị bảng sản phẩm, ẩn bảng doanh thu
        document.getElementById('tableSection_revenue').style.display = 'block';
        document.getElementById('tableSection').style.display = 'none';
    }
    document.getElementById('btnTable').classList.remove('btn-outline-secondary');
    document.getElementById('btnTable').classList.add('btn-secondary');

    document.getElementById('btnChart').classList.remove('btn-primary');
    document.getElementById('btnChart').classList.add('btn-outline-primary');
}
function showdt() {
    const btn = document.getElementById('btnTable');
    const typeSelect = document.getElementById('type');

    typeSelect.addEventListener('change', function () {
        const val = this.value;

        // Chỉ xử lý khi btnTable có class 'btn-secondary' (đang hiển thị bảng)
        if (btn.classList.contains('btn-secondary')) {
            if (val === 'Doanh thu') {
                document.getElementById('tableSection_revenue').style.display = 'none';
                document.getElementById('tableSection').style.display = 'block';
            } else if (val === 'Sản phẩm') {
                document.getElementById('tableSection_revenue').style.display = 'block';
                document.getElementById('tableSection').style.display = 'none';
            }
        }
    });
}
