document.addEventListener('DOMContentLoaded', function () {
loadMemberName();
loadMemberNameAndMemberships();
        const note = getQueryParam("note");
        const orderId = getQueryParam("orderId"); // 12345
        const amount = getQueryParam("amount");
        if (note === "giahan") {
          console.log("Day",orderId,amount);
            giaHanThanhVien(amount);
        } else if (note === "nangcap") {
            nangcapthanhvien(orderId);
        } else {
            console.log("Không có hành động phù hợp.");
        }
});
 function showgiahan() {
        document.getElementById('giahansection').style.display = 'block';
        document.getElementById('nangcapsection').style.display = 'none';
        document.getElementById('btngiahan').classList.remove('btn-outline-primary');
        document.getElementById('btngiahan').classList.add('btn-primary');

        document.getElementById('btnnangcap').classList.remove('btn-secondary');
        document.getElementById('btnnangcap').classList.add('btn-outline-secondary');

    }

    function shownangcap() {
      
        document.getElementById('giahansection').style.display = 'none';
        document.getElementById('nangcapsection').style.display = 'block';

        document.getElementById('btnnangcap').classList.remove('btn-outline-secondary');
        document.getElementById('btnnangcap').classList.add('btn-secondary');

        document.getElementById('btngiahan').classList.remove('btn-primary');
        document.getElementById('btngiahan').classList.add('btn-outline-primary');

    }
function loadMemberNameAndMemberships() {
  $.ajax({
    url: '/api/membershipdetail/nangcap', // API backend
    type: 'GET',
    contentType: 'application/json',
    success: function(response) {
      console.log('Response from getMemberAndMemberships:', response);
    if (response.memberName) {
        $('#membershipName').val(response.memberName);
    } else {
        $('#membershipName').val('');
    }
    const select = $('#durationup');
    select.empty(); // Xóa các option cũ
    select.append('<option value="" selected disabled>Chọn cấp độ</option>');
     const list = response.membershipList;
    if (Array.isArray(list)) {
        list.forEach(function(item) {
                console.log("Item:", item);
                select.append(`<option value="${item.membershipId}">${item.name} - Giá: ${item.price}</option>`);
                });
    }
    },
    error: function(err) {
      console.error('Error fetching member and memberships:', err);
    }
  });
}

// Nếu bạn có thêm API khác, ví dụ getMemberName riêng
function loadMemberName() {
  $.ajax({
    url: '/api/membershipdetail/giahan', // Giả sử endpoint khác
    type: 'GET',
    contentType: 'application/json',
    success: function(response) {
      console.log('Response from getMemberName:', response);
       if (response.date) {
        $('#endDate').val(response.date);
      } else {
        $('#endDate').val('');
      }
        if (response.price) {
        $('#price').val(response.price);
        $('#priceLabel').text('Giá gia hạn trên 1 tháng: ' + response.price + ' VNĐ');
        } else {
        $('#price').val('');
        $('#priceLabel').text('Giá gia hạn');
        }
    },
    error: function(err) {
      console.error('Error fetching member name:', err);
    }
  });
}
function giaHanThanhVien(price)
{
    if (!duration) {

      return;
    }

    $.ajax({
      url: '/api/membershipdetail/xulygiahan', // Đường dẫn đến controller xử lý
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify({
        price: price,
      }),
      success: function(response) {
          Swal.fire({
                        title: "Thành công",
                        text: "Gia hạn Thành công",
                        icon: "success",
                        confirmButtonText: "OK"
                    }).then(() => {
                        window.location.href = "http://localhost:8080/doctor/upmembership";
                    });
      },
      error: function(xhr, status, error) {
        alert("Có lỗi xảy ra khi gia hạn.");
        console.error("Lỗi:", error);
      }
    });
}
function nangcapthanhvien(selectedMembershipId)
{
    if (!selectedMembershipId) {
        return;
      }

     $.ajax({
        url: '/api/membershipdetail/xulynangcap',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ membershipId: selectedMembershipId }),
        success: function(response) {
          Swal.fire({
                        title: "Thành công",
                        text: "Nâng cấp thành công",
                        icon:  "success",
                        confirmButtonText: "OK"
                    }).then(() => {
                        window.location.href = "http://localhost:8080/doctor/upmembership";
                    });
        },
        error: function(xhr, status, error) {
          alert('Đã xảy ra lỗi khi nâng cấp.');
          console.error('Error:', error);
        }
      });
}


function giaHanThanhVienVnPay()
{
    var duration = $('#duration').val(); // Lấy giá trị từ dropdown
    var price= $('#price').val();
    if (!duration) {
                Swal.fire({
                        title: "Thất bại",
                        text: "Vui lòng chọn ngày muốn gia hạn",
                        icon:  "error",
                        confirmButtonText: "OK"
                    });
      return;
    }

    $.ajax({
      url: '/api/membershipdetail/xulygiahanvnpay', // Đường dẫn đến controller xử lý
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify({
        duration: duration,
        price: price,
      }),
      success: function(response) {
        window.location.href = response.url;
      },
      error: function(xhr, status, error) {
        alert("Có lỗi xảy ra khi gia hạn.");
        console.error("Lỗi:", error);
         console.error("Response text:", xhr.responseText);
      }
    });
}
function nangcapthanhvienVnPay()
{
    var selectedMembershipId = document.getElementById("durationup").value;
    if (!selectedMembershipId) {
                Swal.fire({
                        title: "Thất bại",
                        text: "Vui lòng chọn mức muốn nâng cấp",
                        icon:  "error",
                        confirmButtonText: "OK"
                    });
        return;
      }
    if (!checkMembershipLevel()) {
        return; // Dừng nếu không được phép nâng cấp
    }
     $.ajax({
        url: '/api/membershipdetail/xulynangcapvnpay',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ membershipId: selectedMembershipId }),
        success: function(response) {
           window.location.href = response.url;
        },
        error: function(xhr, status, error) {
          alert('Đã xảy ra lỗi khi nâng cấp.');
          console.error('Error:', error);
        }
      });
}
    function getQueryParam(param) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(param);
    }
function checkMembershipLevel() {
    const currentMembership = $('#membershipName').val().toLowerCase().trim();
    const selectedOption = $('#durationup option:selected').text().toLowerCase();
    
    // Lấy tên cấp độ được chọn từ text của option
    let selectedMembershipName = '';
    if (selectedOption.includes('premium')) {
        selectedMembershipName = 'premium';
    } else if (selectedOption.includes('basic')) {
        selectedMembershipName = 'basic';
    } else if (selectedOption.includes('vip')) {
        selectedMembershipName = 'vip';
    }
    
    console.log('Current membership:', currentMembership);
    console.log('Selected membership:', selectedMembershipName);
    
    // Kiểm tra nếu cấp độ hiện tại đã là premium hoặc cao hơn
    if (currentMembership.includes('premium') && selectedMembershipName === 'premium') {
        Swal.fire({
            title: "Thất bại",
            text: "Bạn đã là thành viên Premium. Không thể nâng cấp lên cùng cấp độ.",
            icon: "error",
            confirmButtonText: "OK"
        });
        return false;
    }
    
    // Kiểm tra nếu đang cố gắng hạ cấp
    if (currentMembership.includes('premium') && selectedMembershipName === 'basic') {
        Swal.fire({
            title: "Thất bại",
            text: "Không thể hạ cấp từ Premium xuống Basic.",
            icon: "error",
            confirmButtonText: "OK"
        });
        return false;
    }
    
    // Kiểm tra nếu đã ở cấp độ cao nhất
    if (currentMembership.includes('vip')) {
        Swal.fire({
            title: "Thất bại",
            text: "Bạn đã ở cấp độ cao nhất. Không thể nâng cấp thêm.",
            icon: "error",
            confirmButtonText: "OK"
        });
        return false;
    }
    
    return true;
}
