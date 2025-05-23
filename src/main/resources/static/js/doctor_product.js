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
    url: `/api/products/getallproduct?page=${page}&pagesize=${pagesize}&keyword=${keyword}`,
    method: 'GET',
    success: function (res) {
      console.log(res);
      $("#productTable tbody").empty();
      res.Data.forEach(function (product) {
        if (product.count > 0) {
          var row = "<tr>" +
            "<td>" + product.productId + "</td>" +
            "<td>" + product.name + "</td>" +
            "<td><img src='" + product.image + "' alt='Product Image' style='width: 50px;'></td>" +
            "<td>" + product.description + "</td>" +
            "<td>" + product.price + "</td>" +
            "<td>" + product.count + "</td>" +
            "<td>" +
            "<button class='btn btn-warning btn-sm' onclick='editProduct(this)'>Sửa</button>" +
            "&nbsp;" +
            "<button class='btn btn-danger btn-sm' onclick='deleteProduct(this)'>Xóa</button>" +
            "</td>" +
            "</tr>";
          $("#productTable tbody").append(row);
        }
      });
      // Render pagination here
      renderPagination(page, res.NumberPage, pagesize, keyword);
    },
    error: function (err) {
      console.error("Lỗi:", err);
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

  if (current > 1) {
    paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="LoadData(${current - 1}, ${pagesize}, '${keyword}')">Previous</a></li>`;
  }

  for (let i = 1; i <= total; i++) {
    if (i === current) {
      paginationHTML += `<li class="page-item active"><span class="page-link">${i}</span></li>`;
    } else {
      paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="LoadData(${i}, ${pagesize}, '${keyword}')">${i}</a></li>`;
    }
  }

  if (current < total) {
    paginationHTML += `<li class="page-item"><a class="page-link" href="#" onclick="LoadData(${current + 1}, ${pagesize}, '${keyword}')">Next</a></li>`;
  }

  $("#pagination").html(`<ul class="pagination">${paginationHTML}</ul>`);
}
function onSearch() {
  var keyword = $("#searchInput").val();
  LoadData(1, 2, keyword);
}
function Savedata() {
  var form = $('#productForm')[0];
  var formData = new FormData(form);
  $.ajax({
    url: '/api/products/saveProduct',
    method: 'POST',
    processData: false,
    data: formData,
    contentType: false,
    success: function (res) {
                Swal.fire({
                        title: "Thành công",
                        text: "Thêm sản phẩm thành công",
                        icon:  "success",
                        confirmButtonText: "OK"
                    }).then(() => {
                         window.location.reload();
                    });
    },
    error: function (err) {
            console.log("===== Lỗi khi lưu sản phẩm =====");
      console.log("Status: ", err.status);
      console.log("Response text: ", err.responseText);
      console.log("Full object: ", err);
    }
  });
}
function deleteProduct(button) {
  // Lấy productId từ dòng đang chọn
  var row = $(button).closest('tr');
  var productId = row.find('td:first').text(); // Lấy giá trị productId từ cột đầu tiên

  $.ajax({
    url: `/api/products/updatecount/${productId}`,  // Gửi yêu cầu tới backend để cập nhật count
    method: 'PUT',  // Phương thức PUT vì bạn đang cập nhật dữ liệu
    data: JSON.stringify({ count: 0 }),  // Gửi dữ liệu là count = 0
    contentType: 'application/json',  // Định dạng dữ liệu gửi đi
    success: function (res) {
      // Cập nhật lại dữ liệu trong bảng
      if (res.success) {
                Swal.fire({
                        title: "Thành công",
                        text: "Xóa sản phẩm thành công",
                        icon:  "success",
                        confirmButtonText: "OK"
                    }).then(() => {
                         window.location.reload();
                    });
      } else {
        alert("Có lỗi xảy ra khi xóa sản phẩm!!!");
      }
    },
    error: function (err) {
      console.error("Lỗi:", err);
      alert("Lỗi khi xóa sản phẩm.");
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
function editProduct(button) {
  openEditProductModal(button);
}
function addProduct() {
  // Mở modal thêm sản phẩm
  openAddProductModal();
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
  const productId = currentEditingRow.cells[0].innerText.trim();
  const productName = currentEditingRow.cells[1].innerText.trim();
  const productimage = currentEditingRow.cells[2].querySelector('img').src;
  const productdescription = currentEditingRow.cells[3].innerText.trim();
  const productPrice = currentEditingRow.cells[4].innerText.trim();
  const productcount = currentEditingRow.cells[5].innerText.trim();
  document.getElementById('productId').value=productId;
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