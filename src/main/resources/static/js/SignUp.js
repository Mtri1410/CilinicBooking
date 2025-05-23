
document.addEventListener("DOMContentLoaded",function(){


const emailInput=document.getElementById("form3Example3-email");
const emailerror=document.getElementById("emailError");
const passInput=document.getElementById("form3Example4-password");
const passerror=document.getElementById("passError");
const comfirmpassInput=document.getElementById("form3Example6-comfirmpassword");
const comfirmpasserror=document.getElementById("comfirmpasserror");
const usernameInput=document.getElementById("form3Example5-username");
const usernameerror=document.getElementById("usernameError");
const fullnameInput=document.getElementById("form3Example7-fullname");
const fullnameerror=document.getElementById("FullNameError");
const adressInput=document.getElementById("form3Example8-adress");
const adresserror=document.getElementById("adressError");
const phonenumberInput=document.getElementById("form3Example9-phonenumber");
const phonenumberserror=document.getElementById("phoneNumbererror");

const emailregex=/^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const fullnameregex=/^[A-Za-zÀ-ỹ\s]+$/u;
const usernameregex=/^[A-Za-z0-9]+$/;
const phonenumberregex=/^[0-9]{10}$/;

fullnameInput.addEventListener("blur",function(){
    const fullname=fullnameInput.value.trim();

    if(fullname=="")
    {
        fullnameerror.textContent="Tên không được bỏ trống";
    }
    else if(!fullnameregex.test(fullname))
    {
        fullnameerror.textContent="Tên không được chứa các kí tự đặc biệt";

    }
    else{
        fullnameerror.textContent="";
    }
});

usernameInput.addEventListener("blur",function(){
    const username=usernameInput.value.trim();
    if(username=="")
    {
        usernameerror.textContent="Tên tài khoản không được bỏ trống";

    }
    else if(!usernameregex.test(username))
    {
        usernameerror.textContent="Tên tài khoản không được chứa kí tự đặc biệt";
    }
    else{
        usernameerror.textContent="";
    }
});

passInput.addEventListener("blur",function(){
    const pass=passInput.value.trim();
    if(pass=="")
    {
        passerror.textContent="Mật khẩu không được bỏ trống";
    }
    else
    {
        passerror.textContent="";
    }
});

comfirmpassInput.addEventListener("blur",function(){
    const comfirmpass=comfirmpassInput.value.trim();
    if(comfirmpass=="")
    {
        comfirmpasserror.textContent="Mật khẩu xác nhận không được bỏ trống";

    }
    else if(comfirmpass!=passInput.value.trim())
    {
        comfirmpasserror.textContent="Mật khẩu xác nhận không khớp với mật khẩu";
    }
    else
    {
        comfirmpasserror.textContent="";
    }
});

emailInput.addEventListener("blur",function(){
    const email=emailInput.value.trim();
    if(email=="")
    {
        emailerror.textContent="Email không được bỏ trống";

    }
    else if(!emailregex.test(email))
    {
        emailerror.textContent="Email không hợp lệ";

    }
    else
    {
        emailerror.textContent="";
    }
});

phonenumberInput.addEventListener("blur",function(){
    const phonenumber=phonenumberInput.value.trim();
    if(phonenumber=="")
    {
        phonenumberserror.textContent="Số điện thoại không được bỏ trống";
    }
    else if(!phonenumberregex.test(phonenumber))
    {
        phonenumberserror.textContent="Số điện thoại phải là 10 số và không được chứa kí tự";
    }
    else
    {
        phonenumberserror.textContent="";
    }
});
adressInput.addEventListener("blur",function(){
    const adress=adressInput.value.trim();
    if (adress === "") {
        adresserror.textContent = "Địa chỉ không được bỏ trống";
    }
});

//Submit
const formInput = document.getElementById("signupform");

formInput.addEventListener("submit", function (e) {
    let isValid = true;

    // Email
    const email = emailInput.value.trim();
    if (email === "") {
        emailerror.textContent = "Email không được bỏ trống";
        isValid = false;
    } else if (!emailregex.test(email)) {
        emailerror.textContent = "Email không hợp lệ";
        isValid = false;
    } 
    // Fullname
    const fullname = fullnameInput.value.trim();
    if (fullname === "") {
        fullnameerror.textContent = "Tên không được bỏ trống";
        isValid = false;
    } else if (!fullnameregex.test(fullname)) {
        fullnameerror.textContent = "Tên không được chứa ký tự đặc biệt";
        isValid = false;
    } 
    // Username
    const username = usernameInput.value.trim();
    if (username === "") {
        usernameerror.textContent = "Tên tài khoản không được bỏ trống";
        isValid = false;
    }
    else if(!usernameregex.test(username))
    {
        usernameerror.textContent="Tên tài khoản không được chứa kí tự đặc biệt";
        isValid = false;
    }

    // Password
    const pass = passInput.value.trim();
    if (pass === "") {
        passerror.textContent = "Mật khẩu không được bỏ trống";
        isValid = false;
    }

    // Confirm Password
    const comfirmpass = comfirmpassInput.value.trim();
    if (comfirmpass === "") {
        comfirmpasserror.textContent = "Xác nhận mật khẩu không được bỏ trống";
        isValid = false;
    } else if (comfirmpass !== pass) {
        comfirmpasserror.textContent = "Mật khẩu xác nhận không khớp với mật khẩu";
        isValid = false;
    }

    // Phone number
    const phone = phonenumberInput.value.trim();
    if (phone === "") {
        phonenumberserror.textContent = "Số điện thoại không được bỏ trống";
        isValid = false;
    }
    else if(!phonenumberregex.test(phonenumber))
    {
        phonenumberserror.textContent="Số điện thoại phải là 10 số và không được chứa kí tự";
        isValid = false;
    }

    // Address
    const address = adressInput.value.trim();
    if (address === "") {
        adresserror.textContent = "Địa chỉ không được bỏ trống";
        isValid = false;
    }

    // Ngăn submit nếu có lỗi
    if (!isValid) {
        e.preventDefault(); // Chặn form gửi đi
    }
});


  document.getElementById('togglePassword').addEventListener('click',function(){
    var passwordField=document.getElementById('form3Example4-password');
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
    var passwordField=document.getElementById('form3Example6-comfirmpassword');
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
