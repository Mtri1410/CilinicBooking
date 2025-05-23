let sortStates = [null, null, null, null, null, null]; // 6 cột cho bảng chính
let sortStatesdetail = [null, null, null, null]; // 4 cột cho bảng chi tiết
$(document).ready(function () {
  LoadData();
});
function LoadData(page, pagesize, keyword) {
  page = page || 1;
  pagesize = pagesize || 10;
  keyword = keyword || "";
  $.ajax({
    url: '/api/order/getall',
    method: 'GET',
    dataType: 'json',
    data: {
      page: page,
      pagesize: pagesize,
      keyword: keyword,
    },
    success: function (res) {
      console.log("day");
      $("#orderTable tbody").empty();
      res.Data.forEach(function (order) {
        var buttons = "";
        if (order.status === "PENDING") {
          buttons = "<button class='btn btn-warning btn-sm' onclick='editorder(this)'>Xác nhận</button>" +
            "&nbsp;" +
            "<button class='btn btn-danger btn-sm' onclick='deleteorder(this)'>Hủy</button>";
        }
        let statusText = '';
        let statusClass = '';

        switch (order.status) {
          case 'PENDING':
            statusClass = 'bg-warning text-dark';
            break;
          case 'PAID':
            statusClass = 'bg-success text-white';
            break;
          case 'CANCELLED':
            statusClass = 'bg-danger text-white';
            break;
          default:
            statusClass = 'bg-secondary text-white';
        }
        var row = "<tr>" +
          "<td>" + order.orderId + "</td>" +
          "<td>" + order.patient.fullName + "</td>" +
          "<td>" + order.orderDate + "</td>" +
          "<td>" + order.totalAmount + "</td>" +
          "<td><span class='px-2 py-1 rounded-pill " + statusClass + "'>" + order.status + "</span></td>" +
          "<td>" + buttons + "</td>" +
          "<td>" +
          "<button class='btn btn-outline-secondary' onclick='editDetailOrder(this)'>Chi tiết</button>" +
          "</td>" +
          "</tr>";
        $("#orderTable tbody").append(row);
      });
      renderPagination(page, res.NumberPage, pagesize, keyword);
    },
    error: function (xhr, status, error) {
      console.error("Lỗi:", status, error);
      try {
        var responseData = JSON.parse(xhr.responseText);
        console.log("Dữ liệu phân tích:", responseData);
      } catch (e) {
        console.error("Không thể phân tích JSON:", e);
        console.log("Dữ liệu thô nhận được:", xhr.responseText);
      }
    }
  });
}
function renderPagination(current, total, pagesize, keyword) {
  let paginationHTML = '';
  keyword = keyword || "";

  if (total <= 1) {
    $("#pagination").html('');
    return;
  }

  // Previous
  if (current > 1) {
    paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="LoadData(${current - 1}, ${pagesize}, '${keyword}')">Previous</a></li>`;
  }

  // Page numbers
  for (let i = 1; i <= total; i++) {
    if (i === current) {
      paginationHTML += `<li class="page-item active"><span class="page-link">${i}</span></li>`;
    } else {
      paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="LoadData(${i}, ${pagesize}, '${keyword}')">${i}</a></li>`;
    }
  }

  // Next
  if (current < total) {
    paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="LoadData(${current + 1}, ${pagesize}, '${keyword}')">Next</a></li>`;
  }

  $("#pagination").html(`<ul class="pagination">${paginationHTML}</ul>`);
}
function onSearch() {
  var keyword = $("#searchInput").val();
  LoadData(1, 2, keyword);
}
function LoadOrderDetails(orderId, page, pagesize, keyword) {
  page = page || 1;
  pagesize = pagesize || 10;
  keyword = keyword || "";

  $.ajax({
    url: '/api/order/order-items',
    method: 'GET',
    dataType: 'json',
    data: {
      orderId: orderId,
      page: page,
      pagesize: pagesize,
      keyword: keyword,
    },
    success: function (res) {
      console.log(res);
      $("#orderdetailTable tbody").empty();

      if (res.Data.length === 0) {
        $("#orderdetailTable tbody").append("<tr><td colspan='4' class='text-center'>Không có dữ liệu</td></tr>");
      } else {
        res.Data.forEach(function (item) {
          var row = "<tr>" +
            "<td>" + item.orderItemId + "</td>" +
            "<td>" + item.product.name + "</td>" +
            "<td>" + item.quantity + "</td>" +
            "<td>" + item.price.toLocaleString('vi-VN') + " ₫</td>" +
            "</tr>";
          $("#orderdetailTable tbody").append(row);
        });
      }
      renderOrderDetailPagination(orderId, page, res.NumberPage, keyword);
    },
    error: function (xhr, status, error) {
      console.error("Lỗi:", status, error);
    }
  });
}
function renderOrderDetailPagination(orderId, current, total, keyword) {
  let paginationHTML = '';

  if (total <= 1) {
    $("#paginationOrderDetail").html('');
    return;
  }

  if (current > 1) {
    paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="LoadOrderDetails(${orderId}, ${current - 1}, 2, '${keyword}')">Previous</a></li>`;
  }

  for (let i = 1; i <= total; i++) {
    if (i === current) {
      paginationHTML += `<li class="page-item active"><span class="page-link">${i}</span></li>`;
    } else {
      paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="LoadOrderDetails(${orderId}, ${i}, 2, '${keyword}')">${i}</a></li>`;
    }
  }

  if (current < total) {
    paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="LoadOrderDetails(${orderId}, ${current + 1}, 2, '${keyword}')">Next</a></li>`;
  }

  $("#paginationOrderDetail").html(`<ul class="pagination">${paginationHTML}</ul>`);
}
function editDetailOrder(button) {
  openDetailOrderModal(button);
}

function openDetailOrderModal(button) {
  currentEditingRow = button.closest('tr');
  const id = currentEditingRow.cells[0].innerText.trim();
  document.getElementById('orderdetailModalLabel').innerText = `Chi tiết đơn hàng ${id}`;
  new bootstrap.Modal(document.getElementById('orderdetailModal')).show();
  LoadOrderDetails(id);
}
function editorder(button) {
  // Lấy orderId từ dòng chứa nút (giả sử có orderId trong thẻ <td>)
  var row = button.closest('tr');
  var orderId = row.cells[0].innerText.trim();

  $.ajax({
    url: '/api/order/updateStatus',  // API endpoint bạn tạo bên backend
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({ orderId: orderId, status: 'PAID' }),
    success: function (response) {
                Swal.fire({
                        title: "Thành công",
                        text: "Xác nhận thành công",
                        icon:  "success",
                        confirmButtonText: "OK"
                    }).then(() => {
                         window.location.reload();
                    });
    },
    error: function (err) {
      alert('Lỗi khi xác nhận đơn hàng.');
      console.error(err);
    }
  });
}

function deleteorder(button) {
  var row = button.closest('tr');
  var orderId = row.cells[0].innerText.trim();

  $.ajax({
    url: '/api/order/updateStatus',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({ orderId: orderId, status: 'CANCELLED' }),
    success: function (response) {
                Swal.fire({
                        title: "Thành công",
                        text: "Hủy đơn thành công",
                        icon:  "success",
                        confirmButtonText: "OK"
                    }).then(() => {
                         window.location.reload();
                    });
    },
    error: function (err) {
      alert('Lỗi khi hủy đơn hàng.');
      console.error(err);
    }
  });
}
function sortTable(column) {
  const table = document.getElementById("orderTable");
  const tbody = table.tBodies[0];
  const rows = Array.from(tbody.querySelectorAll("tr"));

  // Đảo chiều sort
  sortStates[column] = sortStates[column] === true ? false : true;
  const asc = sortStates[column];

  rows.sort((a, b) => {
    const aText = a.children[column].innerText.trim();
    const bText = b.children[column].innerText.trim();
    if (!isNaN(aText) && !isNaN(bText)) {
      return asc ? aText - bText : bText - aText;
    }
    return asc ? aText.localeCompare(bText) : bText.localeCompare(aText);
  });

  // Clear tbody và append hàng mới
  tbody.innerHTML = '';
  tbody.append(...rows);

  // Reset icon tất cả cột
  table.querySelectorAll('th.sortable').forEach((th, idx) => {
    const icon = th.querySelector('.sort-icon');
    th.classList.remove('active');
    icon.className = 'sort-icon fa-solid';
    if (icon) {
      th.classList.remove('active');
      icon.className = 'sort-icon fa-solid';
      if (idx === column) {
        th.classList.add('active');
        if (asc) {
          icon.classList.add('fa-sort-up');
        } else {
          icon.classList.add('fa-sort-down');
        }
      } else {
        icon.classList.add('fa-sort'); // Đặt icon mặc định cho các cột không được sort
      }
    }
  });
}
function sortdetailTable(column) {
  const table = document.getElementById("orderdetailTable");
  const tbody = table.tBodies[0];
  const rows = Array.from(tbody.querySelectorAll("tr"));

  // Đảo chiều sort
  sortStatesdetail[column] = sortStatesdetail[column] === true ? false : true;
  const asc = sortStatesdetail[column];

  rows.sort((a, b) => {
    const aText = a.children[column].innerText.trim();
    const bText = b.children[column].innerText.trim();
    if (!isNaN(aText) && !isNaN(bText)) {
      return asc ? aText - bText : bText - aText;
    }
    return asc ? aText.localeCompare(bText) : bText.localeCompare(aText);
  });

  // Clear tbody và append hàng mới
  tbody.innerHTML = '';
  tbody.append(...rows);

  // Reset icon tất cả cột
  table.querySelectorAll('th.sortable').forEach((th, idx) => {
    const icon = th.querySelector('.sort-icon');
    if (icon) {
      th.classList.remove('active');
      icon.className = 'sort-icon fa-solid';
      if (idx === column) {
        th.classList.add('active');
        if (asc) {
          icon.classList.add('fa-sort-up');
        } else {
          icon.classList.add('fa-sort-down');
        }
      } else {
        icon.classList.add('fa-sort'); // Đặt icon mặc định cho các cột không được sort
      }
    }
  });
}