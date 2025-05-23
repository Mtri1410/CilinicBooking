
document.addEventListener("DOMContentLoaded",function(){

});

const urlParams = new URLSearchParams(window.location.search);
const token = urlParams.get("token");
 function resetPassword() {
            var newPassword = $("#newPassword").val();
            console.log("Token:",token,"Pass",newPassword);
            $.ajax({
                url: "/api/auth/reset-password",
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify({ token: token, newPassword: newPassword }),
                success: function(result) {
                    $("#response").text(result.message);
                                          Swal.fire({
                        title: "Thành công",
                        text: "Đổi mật khẩu thành công",
                        icon:  "success",
                        confirmButtonText: "OK"
                    }).then(() => {
                        window.location.href = "http://localhost:8080/SignIn";
                    });

                },
                error: function(xhr) {
                    var error = xhr.responseJSON?.error || "Có lỗi xảy ra";
                    $("#response").text(error);
                }
            });
        }