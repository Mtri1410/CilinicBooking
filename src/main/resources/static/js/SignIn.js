
  document.getElementById('pass-button').addEventListener('click',function(){
    var passwordField=document.getElementById('form3Example4');
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