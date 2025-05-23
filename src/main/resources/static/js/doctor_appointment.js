document.addEventListener('DOMContentLoaded', function() {
// sự kiện gọi vnpay

        const note = getQueryParam("note");
        const date = getQueryParam("orderId"); // 12345
        const amount = getQueryParam("amount");
        const period = getQueryParam("period"); // 12345
        const schedulenote = getQueryParam("schedulenote");
        const userid = getQueryParam("userid"); // 12345
        
        if (note === "pick") {
          console.log("Day",userid,amount,date,period,schedulenote);
            comfirmsaveSchedule(userid,amount,period,date,schedulenote);
        } else {
            console.log("Không có hành động phù hợp.");
        }



///////////




// sự kiện load input phone number

const phoneInput = document.getElementById('patientPhone');
    
    if (phoneInput) {
        // Sự kiện khi nhấn Enter
        phoneInput.addEventListener('keypress', function(event) {
            if (event.key === 'Enter') {
                event.preventDefault(); // Ngăn form submit
                searchPatientByPhone(this.value.trim());
            }
        });
        
        // Sự kiện khi rời khỏi input (blur)
        phoneInput.addEventListener('blur', function() {
            searchPatientByPhone(this.value.trim());
        });
        
        // Sự kiện khi thay đổi giá trị (tùy chọn - để reset tên khi sdt thay đổi)
        phoneInput.addEventListener('input', function() {
            const patientNameInput = document.getElementById('patientName');
            // Reset tên bệnh nhân khi số điện thoại thay đổi
            if (patientNameInput.readOnly) {
                patientNameInput.value = '';
                patientNameInput.readOnly = false;
                patientNameInput.classList.remove('bg-light');
            }
        });
    }





//////////////////////////


    const datePicker = document.getElementById('datePicker');
    const prevWeek = document.getElementById('prevWeek');
    const nextWeek = document.getElementById('nextWeek');
    const displayWeekRange = document.getElementById('displayWeekRange');
    const weekDaysContainer = document.getElementById('weekDaysContainer');
    
    // Lấy ngày thứ Hai của tuần hiện tại
    function getMonday(date) {
        const day = date.getDay();
        // Trong JavaScript, ngày trong tuần bắt đầu từ 0 (Chủ Nhật) đến 6 (Thứ Bảy)
        // Nếu ngày là Chủ Nhật (0), cần lùi về 6 ngày để có thứ Hai tuần trước
        const diff = date.getDate() - day + (day === 0 ? -6 : 1);
        return new Date(date.setDate(diff));
    }
    
    // Lấy các ngày trong tuần từ thứ Hai đến Chủ Nhật
    function getWeekDays(monday) {
        const weekDays = [];
        const tempDate = new Date(monday);
        
        for (let i = 0; i < 7; i++) {
            weekDays.push(new Date(tempDate));
            tempDate.setDate(tempDate.getDate() + 1);
        }
        
        return weekDays;
    }
    
    // Các khung giờ trong ngày - map với period
    const timeSlots = [
        { period: 1, start: "7:00", end: "8:30" },
        { period: 2, start: "8:30", end: "10:00" },
        { period: 3, start: "10:00", end: "11:30" },
        { period: 4, start: "13:00", end: "14:30" },
        { period: 5, start: "14:30", end: "16:00" },
        { period: 6, start: "16:00", end: "17:30" }
    ];
    
    // Lưu trữ dữ liệu lịch hẹn đã tải
    let appointmentsData = [];
    
    // Cập nhật bảng lịch
    function updateScheduleTable(weekDays) {
        const scheduleTable = document.getElementById('scheduleTable');
        const tableHead = scheduleTable.querySelector('thead tr');
        const tableBody = scheduleTable.querySelector('tbody');
        
        // Xóa tất cả các cột ngày hiện tại trừ cột time
        while (tableHead.children.length > 1) {
            tableHead.removeChild(tableHead.lastChild);
        }
        
        // Xóa tất cả hàng khung giờ
        tableBody.innerHTML = '';
        
        // Thêm các cột ngày trong tuần
        const today = new Date().setHours(0, 0, 0, 0);
        const dayNames = ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'CN'];
        
        weekDays.forEach((day, index) => {
            const th = document.createElement('th');
            
            // Kiểm tra nếu là ngày hiện tại
            if (day.setHours(0, 0, 0, 0) === today) {
                th.classList.add('current-day-header');
            }
            
            const formattedDate = `${String(day.getDate()).padStart(2, '0')}/${String(day.getMonth() + 1).padStart(2, '0')}`;
            th.innerHTML = `${dayNames[index]}<br>${formattedDate}`;
            
            tableHead.appendChild(th);
        });
        



        timeSlots.forEach(slot => {
        const row = document.createElement('tr');
        
        // Cột khung giờ
        const timeCell = document.createElement('td');
        timeCell.className = 'time-slot';
        timeCell.textContent = `${slot.start} - ${slot.end}`;
        row.appendChild(timeCell);
        
        // Thêm các ô lịch cho mỗi ngày
        weekDays.forEach((day, dayIndex) => {
            const cell = document.createElement('td');
            cell.className = 'schedule-cell';
            cell.dataset.day = dayIndex;
            cell.dataset.period = slot.period;
            cell.dataset.date = formatDateForData(day);
            
            // Kiểm tra xem ô này có lịch hẹn không
            const appointmentData = findAppointmentForDateAndPeriod(day, slot.period);
            if (appointmentData) {
                // Ô có lịch hẹn - hiển thị thông tin lịch hẹn
                cell.classList.add('has-appointment');
                
                // Thêm class dựa vào trạng thái (PENDING, CONFIRMED, CANCELLED)
                cell.classList.add(`status-${appointmentData.status.toLowerCase()}`);
                
                // Hiển thị tên bệnh nhân (hoặc thông tin khác)
                cell.innerHTML = `
                    <div class="appointment-info">
                        <div class="patient-name">${appointmentData.patient.fullName || 'Bệnh nhân'}</div>
                        <div class="status">${getStatusLabel(appointmentData.status)}</div>
                    </div>
                `;
                cell.dataset.appointmentId = appointmentData.appointmentId;
                
                // Thêm sự kiện click để xem chi tiết lịch hẹn
                cell.addEventListener('click', function() {
                    showAppointmentDetails(appointmentData);
                });
            } else {
                // Ô trống - hiển thị dấu + và thêm sự kiện click để tạo lịch hẹn mới
                    const cellDate = new Date(day);
                    cellDate.setHours(0, 0, 0, 0); // reset giờ để so sánh chính xác

                    if (cellDate >= today) {
                        // Chỉ thêm dấu + và sự kiện nếu ngày là hôm nay hoặc tương lai
                        cell.classList.add('empty-slot');
                        cell.innerHTML = '<div class="empty-slot-content">+</div>';

                        cell.addEventListener('click', function() {
                            openCreateAppointmentModal(cell);
                        });
                    } else {
                        // Không cho tạo lịch ở ngày quá khứ
                        cell.classList.add('past-slot');
                        cell.innerHTML = ''; // không hiển thị dấu +
                    }
            }
            
            row.appendChild(cell);
        });
        
        tableBody.appendChild(row);
    });
    }
    
    // Chuyển đổi status thành nhãn tiếng Việt
    function getStatusLabel(status) {
        switch(status) {
            case 'PENDING': return 'Chờ xác nhận';
            case 'CONFIRMED': return 'Đã xác nhận';
            case 'CANCELLED': return 'Đã hủy';
            default: return status;
        }
    }
    
    // Định dạng ngày cho dữ liệu (YYYY-MM-DD)
    function formatDateForData(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }
    
    // Tìm dữ liệu lịch hẹn cho ngày và period cụ thể
    function findAppointmentForDateAndPeriod(date, period) {
        const dateStr = formatDateForData(date);
        
        return appointmentsData.find(appointment => {
            // Ngày trong lịch hẹn được lấy từ schedule.scheduleDate
            const appointmentDate = appointment.schedule.date.split('T')[0]; // Lấy phần ngày từ ISO date string
            return appointmentDate === dateStr && appointment.period === period;
        });
    }
    
    // Hiển thị chi tiết lịch hẹn
    function showAppointmentDetails(appointmentData) {
        console.log('Chi tiết lịch hẹn:', appointmentData);
        
        // Tìm thông tin khung giờ dựa vào period
        const timeSlot = timeSlots.find(slot => slot.period === appointmentData.period);
        
        // Định dạng thông tin thời gian
        const appointmentDate = new Date(appointmentData.schedule.scheduleDate);
        const formattedDate = formatDateDisplay(appointmentDate);
        const timeRange = timeSlot ? `${timeSlot.start} - ${timeSlot.end}` : 'Không xác định';

        document.getElementById('modalAppointmentId').textContent = appointmentData.appointmentId;
        document.getElementById('modalPatientName').textContent = appointmentData.patient.fullName || 'Không xác định';
        document.getElementById('modalAppointmentDate').textContent = formattedDate;
        document.getElementById('modalTimeRange').textContent = timeRange;
        document.getElementById('modalStatus').textContent = getStatusLabel(appointmentData.status);
        document.getElementById('modalBookingTime').textContent = formatDateTime(appointmentData.bookingTime);
        
        const depositSection = document.getElementById('depositSection');
        const modalDepositAmount = document.getElementById('modalDepositAmount');
        // Hiển thị thông tin chi tiết (có thể thay thế bằng modal đẹp hơn)
         if (appointmentData.depositAmount) {
        modalDepositAmount.textContent = new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(appointmentData.depositAmount);
            depositSection.style.display = 'block';
        } else {
            depositSection.style.display = 'none';
        }
        
        // Hiển thị modal
        const modal = new bootstrap.Modal(document.getElementById('appointmentDetailsModal'));
        modal.show();
    }
    
    // Định dạng ngày giờ đầy đủ
    function formatDateTime(dateTimeStr) {
        if (!dateTimeStr) return 'Không xác định';
        
        const date = new Date(dateTimeStr);
        
        return `${String(date.getDate()).padStart(2, '0')}/${String(date.getMonth() + 1).padStart(2, '0')}/${date.getFullYear()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    }
    
    // Định dạng ngày cho hiển thị
    function formatDateDisplay(date) {
        const options = { day: '2-digit', month: '2-digit', year: 'numeric' };
        return date.toLocaleDateString('vi-VN', options);
    }
    
    // Cập nhật hiển thị phạm vi tuần
    function updateWeekRange(monday) {
        const sunday = new Date(monday);
        sunday.setDate(monday.getDate() + 6);
        
        displayWeekRange.textContent = `${formatDateDisplay(monday)} - ${formatDateDisplay(sunday)}`;
    }
    
    // Định dạng ngày cho input date (YYYY-MM-DD)
    function formatDateForInput(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }
    
    // Tải dữ liệu lịch hẹn từ server
function loadAppointmentsData(startDate, endDate) {
    // Định dạng ngày cho API
    const formattedStartDate = formatDateForData(startDate);
    const formattedEndDate = formatDateForData(endDate);
    
    // Gọi API để lấy dữ liệu lịch hẹn
    fetch(`/doctor/appointments/get?startDate=${formattedStartDate}&endDate=${formattedEndDate}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Không thể tải dữ liệu lịch hẹn');
        }
        
        // Lấy raw text trước để kiểm tra
        return response.text();
    })
    .then(text => {
        console.log('Raw response:', text); // Debug để xem response thực tế
        
        // Thử parse JSON
        try {
            const data = JSON.parse(text);
            console.log("Dữ liệu lịch hẹn đã tải:", data);
            
            // Lưu dữ liệu lịch hẹn
            appointmentsData = data;
            
            // Cập nhật lại bảng lịch với dữ liệu mới
            const weekDays = getWeekDays(startDate);
            updateScheduleTable(weekDays);
            
        } catch (parseError) {
            console.error('JSON Parse Error:', parseError);
            console.error('Raw response that caused error:', text);
            
            // Hiển thị thông báo lỗi user-friendly
            alert('Dữ liệu từ server không hợp lệ. Vui lòng liên hệ admin.');
            
            // Set empty data để tránh crash
            appointmentsData = [];
            const weekDays = getWeekDays(startDate);
            updateScheduleTable(weekDays);
        }
    })
    .catch(error => {
        console.error('Lỗi khi tải dữ liệu lịch hẹn:', error);
        alert('Có lỗi xảy ra khi tải dữ liệu lịch hẹn. Vui lòng thử lại sau.');
        
        // Set empty data để tránh crash
        appointmentsData = [];
        const weekDays = getWeekDays(startDate);
        updateScheduleTable(weekDays);
    });
}
    

    
    // Cập nhật tất cả dựa trên ngày được chọn
    function updateAllDisplays(selectedDate) {
        const monday = getMonday(selectedDate);
        const weekDays = getWeekDays(monday);
        
        updateWeekRange(monday);
        updateScheduleTable(weekDays);
        
        // Dữ liệu tuần cho việc xử lý dữ liệu
        const weekData = {
            startDate: monday,
            endDate: weekDays[6],
            weekDays: weekDays
        };
        
        // Tải dữ liệu lịch hẹn từ server
        loadAppointmentsData(weekData.startDate, weekData.endDate);
        
        return weekData;
    }
    
    // Khởi tạo với ngày hiện tại
    const today = new Date();
    datePicker.value = formatDateForInput(today);
    const initialWeekData = updateAllDisplays(today);
    
    // Sự kiện khi thay đổi ngày
    datePicker.addEventListener('change', function() {
        const selectedDate = new Date(this.value);
        updateAllDisplays(selectedDate);
    });
    
    // Sự kiện khi nhấn nút tuần trước
    prevWeek.addEventListener('click', function() {
        const currentDate = new Date(datePicker.value);
        currentDate.setDate(currentDate.getDate() - 7);
        datePicker.value = formatDateForInput(currentDate);
        updateAllDisplays(currentDate);
    });
    
    // Sự kiện khi nhấn nút tuần sau
    nextWeek.addEventListener('click', function() {
        const currentDate = new Date(datePicker.value);
        currentDate.setDate(currentDate.getDate() + 7);
        datePicker.value = formatDateForInput(currentDate);
        updateAllDisplays(currentDate);
    });
  function openCreateAppointmentModal(cell) {
    const form = document.getElementById("appointmentForm");
    if (!form) {
        console.warn("Không tìm thấy #appointmentForm");
        return; // Thoát hàm luôn để tránh lỗi
    }
      form.reset();
    const day = parseInt(cell.dataset.day);
    const period = parseInt(cell.dataset.period);
    const dateStr = cell.dataset.date;
    
    // Tìm thông tin khung giờ
    const timeSlot = timeSlots.find(slot => slot.period === period);
    
    // Chuyển đổi ngày từ string sang Date object
    const appointmentDate = new Date(dateStr);
    const formattedDate = formatDateDisplay(appointmentDate);
    const timeRange = timeSlot ? `${timeSlot.start} - ${timeSlot.end}` : 'Không xác định';
    document.getElementById('appointmentForm').reset();

    // Điền thông tin vào modal
    document.getElementById('appointmentDate').value = formattedDate;
    document.getElementById('appointmentTime').value = timeRange;
    document.getElementById('selectedPeriod').value = period;
    document.getElementById('selectedDate').value = dateStr;
    
    // Reset form
    document.getElementById('appointmentForm').reset();
    document.getElementById('appointmentDate').value = formattedDate;
    document.getElementById('appointmentTime').value = timeRange;
    document.getElementById('selectedPeriod').value = period;
    document.getElementById('selectedDate').value = dateStr;
    
    // Mở modal
    const modal = new bootstrap.Modal(document.getElementById('appointmentModal'));
    modal.show();
}

});
function searchPatientByPhone(phoneNumber) {
    if (!phoneNumber || phoneNumber.trim() === '') {
        return;
    }
    
    $.ajax({
        url: `/doctor/appointments/searchbyphone`,
        type: 'GET',
        data: {
            phone: phoneNumber
        },
        dataType: 'json',
        success: function(response) {
             const patient = response.Data;
            const patientNameInput = document.getElementById('patientName');
            const patientIdInput = document.getElementById('patientId');
            if (patient) {
                // Tìm thấy bệnh nhân - điền tên vào input
                patientNameInput.value = patient.fullName || patient.name || '';
                patientNameInput.readOnly = true; // Khóa input tên để không chỉnh sửa
                patientNameInput.classList.add('bg-light'); // Thêm class để hiển thị là readonly
                patientIdInput.value = patient.userId || patient.user_Id || '';
                // Có thể hiển thị thông báo thành công
                showNotification('Đã tìm thấy bệnh nhân: ' + patient.fullName, 'success');
            } else {
                // Không tìm thấy bệnh nhân - cho phép nhập tên mới
                patientNameInput.value = '';
                patientIdInput.value =  '';
                patientNameInput.readOnly = false;
                patientNameInput.classList.remove('bg-light');
                patientNameInput.focus(); // Focus vào input tên để nhập
                
                // Hiển thị thông báo
                showNotification('Không tìm thấy bệnh nhân. Vui lòng nhập tên bệnh nhân mới.', 'info');
            }
        },
        error: function(xhr, status, error) {
            const patientNameInput = document.getElementById('patientName');
            
            if (xhr.status === 404) {
                // Không tìm thấy bệnh nhân
                patientNameInput.value = '';
                patientNameInput.readOnly = false;
                patientNameInput.classList.remove('bg-light');
                patientNameInput.focus();
                
                showNotification('Không tìm thấy bệnh nhân. Vui lòng nhập tên bệnh nhân mới.', 'info');
            } else {
                // Lỗi khác
                console.error('Lỗi khi tìm kiếm bệnh nhân:', error);
                
                // Reset input tên khi có lỗi
                patientNameInput.readOnly = false;
                patientNameInput.classList.remove('bg-light');
                
                showNotification('Có lỗi xảy ra khi tìm kiếm bệnh nhân. Vui lòng thử lại.', 'error');
            }
        }
    });
}
function showNotification(message, type = 'info') {
    // Có thể sử dụng toast, alert hoặc một notification system khác
    console.log(`[${type.toUpperCase()}] ${message}`);
    
    // Ví dụ với toast Bootstrap (nếu có)
    // const toast = document.createElement('div');
    // toast.className = `alert alert-${type} alert-dismissible fade show`;
    // toast.innerHTML = `${message}<button type="button" class="btn-close" data-bs-dismiss="alert"></button>`;
    // document.body.appendChild(toast);
}
function saveSchedule()
{
    var id = $('#patientId').val();
    var date = $('#appointmentDate').val();
    var period = $('#selectedPeriod').val();
    var note = $('#appointmentNote').val();
    var ammout="100000";
    console.log("đây rồi");
    console.log("ssssssssssss",{id, date, period, note}); 
      $.ajax({
                url: '/doctor/appointments/VnPaySchedule',
                type: 'POST',
                data: {
                    date: date,
                    period: period,
                    id: id,
                    note: note,
                    ammout: ammout
                },
                success: function (response) {
                    if (response.url) {
                        window.location.href = response.url; // chuyển hướng đến trang thanh toán VnPay
                    } else {
                        alert("Không nhận được URL thanh toán.");
                    }
                },
                error: function (xhr) {
                    alert("Lỗi khi gửi lịch hẹn: " + xhr.responseText);
                }
            });
}
function comfirmsaveSchedule(id,ammout,period,date,note)
{
    $.ajax({
      url: '/doctor/appointments/Addschedule', // Đường dẫn đến controller xử lý
      type: 'POST',
      data: {
        date: date,
        period: period,
        id: id,
        note: note,
        ammout: ammout
      },
      success: function(response) {
          Swal.fire({
                        title: "Thành công",
                        text: "Đặt lịch Thành công",
                        icon: "success",
                        confirmButtonText: "OK"
                    }).then(() => {
                        window.location.href = "http://localhost:8080/doctor/appointment";
                    });
      },
      error: function(xhr, status, error) {
        alert("Có lỗi xảy ra khi gia hạn.");
        console.error("Lỗi:", error);
      }
    });
}
    function getQueryParam(param) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(param);
    }