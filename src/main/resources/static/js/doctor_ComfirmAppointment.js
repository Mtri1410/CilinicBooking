let sortStates = [null, null]; // null = chưa sort, true = asc, false = desc
$(document).ready(function () {
  LoadData();

  let currentEditingRow = null;
});
// function getCsrfToken() {
//   return $("meta[name='_csrf']").attr("content");
// }
function LoadData(page, pagesize, keyword) {
  page = page || 1;
  pagesize = pagesize || 10;
  keyword = keyword || "";
  $.ajax({
    url: "/doctor/appointments/confirmappointments",
    method: "GET",
    data: {
      page: page,
      pagesize: pagesize,
      keyword: keyword
    },
    success: function (res) {
      console.log(res);
      $("#appointmentsTable tbody").empty();

      res.Data.forEach(function (appointment) {
        // Lấy thông tin bệnh nhân từ object patient
        var patientName = appointment.patient ? appointment.patient.fullName || appointment.patient.name || "N/A" : "N/A";

        // Format ngày đặt và thời gian đặt
        var bookingDate = new Date(appointment.bookingTime);
        var dateStr = bookingDate.toLocaleDateString();
        var period = appointment.period;
        var timeStr = "";
        switch (period) {
          case 1:
            timeStr = "7h - 8h30";
            break;
          case 2:
            timeStr = "8h30 - 10h";
            break;
          case 3:
            timeStr = "10h - 11h30";
            break;
          case 4:
            timeStr = "13h - 14h30";
            break;
          case 5:
            timeStr = "14h30 - 16h";
            break;
          case 6:
            timeStr = "16h - 17h30";
            break;
          default:
            timeStr = "Thời gian không xác định";
        }
        var status = appointment.status || "";

        // Giá (depositAmount) nếu có, nếu không thì 0
        var price = appointment.depositAmount != null ? appointment.depositAmount : 0;
        var buttons = "";
        if (status === "PENDING") {
          buttons = "<button class='btn btn-warning btn-sm' onclick='confirmAppointment(this)'>Xác nhận</button>" +
            "&nbsp;" +
            "<button class='btn btn-danger btn-sm' onclick='cancelAppointment(this)'>Hủy</button>";
        }
        let statusText = '';
        let statusClass = '';

        switch (status) {
          case 'PENDING':
            statusClass = 'bg-warning text-dark';
            break;
          case 'CONFIRMED':
            statusClass = 'bg-success text-white';
            break;
          case 'CANCELLED':
            statusClass = 'bg-danger text-white';
            break;
          default:
            statusText = status;
            statusClass = 'bg-secondary text-white';
        }

        var row = `<tr>
                    <td>${appointment.appointmentId}</td>
                    <td>${patientName}</td>
                    <td>${dateStr}</td>
                    <td>${timeStr}</td>
                    <td>${price.toFixed(2)}</td>
                    <td ><span class="px-2 py-1 rounded-pill ${statusClass}">${status}</span></td>
                    <td>${buttons}</td>
                </tr>`;

        $("#appointmentsTable tbody").append(row);
      });

      renderPagination(page, res.NumberPage, pagesize, keyword);
    },
    error: function (err) {
      console.error("Lỗi khi lấy dữ liệu:", err);
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
function confirmAppointment(button) {
  var row = button.closest('tr');
  var appointmentId = row.cells[0].innerText.trim();

  $.ajax({
    url: '/doctor/appointments/updateStatus',  // API endpoint backend bạn tạo để update status appointment
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({ appointmentId: appointmentId, status: 'CONFIRMED' }),
    success: function (response) {
                Swal.fire({
                        title: "Thành công",
                        text: "Xác nhận cuộc hẹn thành công",
                        icon:  "success",
                        confirmButtonText: "OK"
                    }).then(() => {
                         window.location.reload();
                    });
    },
    error: function (err) {
      alert('Lỗi khi xác nhận cuộc hẹn.');
      console.error(err);
    }
  });
}

function cancelAppointment(button) {
  var row = button.closest('tr');
  var appointmentId = row.cells[0].innerText.trim();

  $.ajax({
    url: '/doctor/appointments/updateStatus',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({ appointmentId: appointmentId, status: 'CANCELLED' }),
    success: function (response) {
                Swal.fire({
                        title: "Thành công",
                        text: "Hủy cuộc hẹn thành công",
                        icon:  "success",
                        confirmButtonText: "OK"
                    }).then(() => {
                         window.location.reload();
                    });
    },
    error: function (err) {
      alert('Lỗi khi hủy cuộc hẹn.');
      console.error(err);
    }
  });
}


function sortTable(column) {
  const table = document.getElementById("productTable");
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
  document.querySelectorAll('th.sortable').forEach((th, idx) => {
    const icon = th.querySelector('.sort-icon');
    th.classList.remove('active');
    icon.className = 'sort-icon fa-solid';
    if (idx === column) {
      th.classList.add('active');
      if (asc) {
        icon.classList.add('fa-sort-up');
      } else {
        icon.classList.add('fa-sort-down');
      }
    }
  });
}

function addProduct() {
  // Mở modal thêm sản phẩm
  openAddProductModal();
}
function editProduct(button) {
  openEditProductModal(button);
}
// Mở modal để Thêm sản phẩm
function openAddProductModal() {
  currentEditingRow = null;
  document.getElementById('productForm').reset(); // Xóa trắng form
  document.getElementById('productModalLabel').innerText = 'Thêm sản phẩm';
  new bootstrap.Modal(document.getElementById('productModal')).show();
}

// Mở modal để Sửa sản phẩm
function openEditProductModal(button) {
  currentEditingRow = button.closest('tr');

  document.getElementById('productForm').reset(); // Reset trước nếu cần

  const productName = currentEditingRow.cells[1].innerText.trim();
  const productimage = currentEditingRow.cells[2].querySelector('img').src;
  const productdescription = currentEditingRow.cells[3].innerText.trim();
  const productPrice = currentEditingRow.cells[4].innerText.trim();
  const productcount = currentEditingRow.cells[5].innerText.trim();

  document.getElementById('productName').value = productName;
  document.getElementById('productPrice').value = productPrice;
  document.getElementById('productCount').value = productcount;
  document.getElementById('productdescription').value = productdescription;
  document.getElementById('imagePreview').src = productimage;
  document.getElementById('imagePreview').style.display = 'block';

  document.getElementById('productModalLabel').innerText = 'Sửa sản phẩm';
  new bootstrap.Modal(document.getElementById('productModal')).show();
}
document.getElementById('productimage').addEventListener('change', function (event) {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = function (e) {
      document.getElementById('imagePreview').src = e.target.result;
      document.getElementById('imagePreview').style.display = 'block';
    };
    reader.readAsDataURL(file);
  }
});