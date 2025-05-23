document.addEventListener('DOMContentLoaded', function () {
    const check=document.getElementById('changepass');
    const passwordFields = [
    document.getElementById('currentPassword'),
    document.getElementById('newPassword'),
    document.getElementById('confirmPassword')
    ];
    const toggleBtn = document.getElementById("detailmebership");
    const membershipList = document.getElementById("membershipList");
    toggleBtn.addEventListener("click",function(){
        if (membershipList.style.display === "none") {
        membershipList.style.display = "block";
      } else {
        membershipList.style.display = "none";
      }
    });

 function togglePasswordFields() {
      const isChecked = check.checked;
      passwordFields.forEach(field => {
        field.readOnly = !isChecked;
           if (!isChecked) {
      field.value = ''; // Xóa nội dung nếu checkbox bị tắt
    }
      });
 
    }
togglePasswordFields();
    check.addEventListener('change', togglePasswordFields);



  document.getElementById('passcurrent').addEventListener('click',function(){
    var passwordField=document.getElementById('currentPassword');
    var icon = this.querySelector('i');
    if(passwordField.type=='password')
    {
      passwordField.type='text';
      icon.classList.remove('fa-eye-slash');
      icon.classList.add('fa-eye');
    }
    else
    {
        passwordField.type = 'password';
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    }
  });

  document.getElementById('toggleNewPassword').addEventListener('click',function(){
    var passwordField=document.getElementById('newPassword');
    var icon = this.querySelector('i');
    if(passwordField.type=='password')
    {
      passwordField.type='text';
      icon.classList.remove('fa-eye-slash');
      icon.classList.add('fa-eye');
    }
    else
    {
        passwordField.type = 'password';
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    }
  });

    document.getElementById('toggleConfirmPassword').addEventListener('click',function(){
    var passwordField=document.getElementById('confirmPassword');
    var icon = this.querySelector('i');
    if(passwordField.type=='password')
    {
      passwordField.type='text';
      icon.classList.remove('fa-eye-slash');
      icon.classList.add('fa-eye');
    }
    else
    {
        passwordField.type = 'password';
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    }
  });





  });
