document.addEventListener('DOMContentLoaded', function() {
    // Constants
    const MORNING_START = '07:00';
    const MORNING_END = '11:30';
    const AFTERNOON_START = '13:00';
    const AFTERNOON_END = '17:30';
    const SLOT_DURATION = 90; // 1 tiếng 30 phút (90 phút)

    // Elements
    const dateInput = document.getElementById('appointmentDate');
    const timeSlots = document.querySelectorAll('.time-slot');
    const selectedTimeInput = document.getElementById('selectedTime');
    const bookingForm = document.getElementById('bookingForm');
    const doctorId = document.querySelector('input[name="doctorId"]').value;

    // Thiết lập ngày tối thiểu là ngày hiện tại
    function setupDateConstraints() {
        const today = new Date();
        const tomorrow = new Date(today);
        tomorrow.setDate(tomorrow.getDate() + 1);
        dateInput.min = tomorrow.toISOString().split('T')[0];
        
        // Giới hạn đặt lịch tối đa 30 ngày từ hiện tại
        const maxDate = new Date(today);
        maxDate.setDate(maxDate.getDate() + 30);
        dateInput.max = maxDate.toISOString().split('T')[0];
    }

    // Kiểm tra xem thời gian có hợp lệ không
    function isValidTime(time) {
        const [hours, minutes] = time.split(':').map(Number);
        const timeValue = hours * 60 + minutes;

        const morningStart = parseTimeToMinutes(MORNING_START);
        const morningEnd = parseTimeToMinutes(MORNING_END);
        const afternoonStart = parseTimeToMinutes(AFTERNOON_START);
        const afternoonEnd = parseTimeToMinutes(AFTERNOON_END);

        return (timeValue >= morningStart && timeValue <= morningEnd) ||
               (timeValue >= afternoonStart && timeValue <= afternoonEnd);
    }

    // Chuyển đổi thời gian sang phút
    function parseTimeToMinutes(time) {
        const [hours, minutes] = time.split(':').map(Number);
        return hours * 60 + minutes;
    }

    // Kiểm tra xem slot có available không
    async function checkSlotAvailability(date, time) {
        try {
            const response = await fetch(`/api/appointments/check-availability?doctorId=${doctorId}&date=${date}&time=${time}`);
            const data = await response.json();
            return data.available;
        } catch (error) {
            console.error('Error checking slot availability:', error);
            return false;
        }
    }

    // Cập nhật trạng thái của các time slots
    async function updateTimeSlots(selectedDate) {
        for (const slot of timeSlots) {
            const time = slot.getAttribute('data-time');
            const isAvailable = await checkSlotAvailability(selectedDate, time);
            
            if (!isAvailable) {
                slot.classList.add('disabled');
                slot.title = 'Khung giờ này đã được đặt';
            } else {
                slot.classList.remove('disabled');
                slot.title = '';
            }
        }
    }

    // Xử lý khi chọn ngày
    dateInput.addEventListener('change', async function() {
        const selectedDate = this.value;
        
        // Reset selected time
        selectedTimeInput.value = '';
        timeSlots.forEach(slot => slot.classList.remove('selected'));
        
        // Kiểm tra xem có phải ngày nghỉ/ngày lễ không
        try {
            const response = await fetch(`/api/holidays/check?date=${selectedDate}`);
            const data = await response.json();
            
            if (data.isHoliday) {
                alert('Ngày bạn chọn là ngày nghỉ/ngày lễ. Vui lòng chọn ngày khác.');
                this.value = '';
                return;
            }
        } catch (error) {
            console.error('Error checking holiday:', error);
        }

        // Cập nhật trạng thái các time slots
        await updateTimeSlots(selectedDate);
    });

    // Xử lý khi chọn time slot
    timeSlots.forEach(slot => {
        slot.addEventListener('click', function() {
            if (!this.classList.contains('disabled')) {
                // Bỏ chọn tất cả các slot khác
                timeSlots.forEach(s => s.classList.remove('selected'));
                // Chọn slot hiện tại
                this.classList.add('selected');
                // Cập nhật giá trị input hidden
                selectedTimeInput.value = this.getAttribute('data-time');
            }
        });
    });

    // Validate form trước khi submit
    bookingForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        if (!dateInput.value) {
            alert('Vui lòng chọn ngày khám');
            return;
        }

        if (!selectedTimeInput.value) {
            alert('Vui lòng chọn thời gian khám');
            return;
        }

        // Kiểm tra lại availability trước khi submit
        const isAvailable = await checkSlotAvailability(dateInput.value, selectedTimeInput.value);
        if (!isAvailable) {
            alert('Khung giờ này vừa được đặt. Vui lòng chọn khung giờ khác.');
            await updateTimeSlots(dateInput.value);
            return;
        }

        // Hiển thị dialog xác nhận
        if (confirm('Bạn có chắc chắn muốn đặt lịch khám vào thời gian này không?')) {
            // Submit form
            this.submit();
        }
    });

    // Initialize
    setupDateConstraints();
}); 