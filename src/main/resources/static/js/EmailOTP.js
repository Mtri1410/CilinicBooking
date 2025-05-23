function sendResetEmail() {
    var email = $("#email").val();
    
    console.log("Sending request to:", "/api/auth/forgot-password");
    console.log("Email:", email);
    
    $.ajax({
        url: "/api/auth/forgot-password",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ email: email }),
        beforeSend: function(xhr) {
            console.log("Request headers:", xhr);
        },
        success: function(result, textStatus, xhr) {
            // console.log("Response status:", xhr.status);
            // console.log("Response headers:", xhr.getAllResponseHeaders());
            // console.log("Success result:", result);
            // console.log("Type of result:", typeof result);
                      Swal.fire({
                        title: "Thành công",
                        text: "Gửi mã xác nhận thành công vui lòng check email",
                        icon:  "success",
                        confirmButtonText: "OK"
                    });
                    
            // Kiểm tra xem result có phải object không
            if (typeof result === 'object' && result.message) {
                $("#response").text(result.message);
            } else {
                $("#response").text("Email đã được gửi!");
            }
        },
        error: function(xhr) {
            console.log("Error status:", xhr.status);
            console.log("Error response:", xhr.responseText);
            var error = xhr.responseJSON?.error || "Có lỗi xảy ra";
            $("#response").text(error);
        }
    });
}