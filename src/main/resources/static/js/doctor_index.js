var el = document.getElementById("wrapper");
var toggleButton = document.getElementById("menu-toggle");

toggleButton.onclick = function () {
    el.classList.toggle("toggled");
};
const sidebar=document.querySelector('.side-bar-active');
const items=sidebar.querySelectorAll('a');
const currentPath = window.location.pathname;

items.forEach(item =>{
    const itemPath = item.getAttribute('href');
    if (itemPath==currentPath) {
      item.classList.add('active');
    } else {
      item.classList.remove('active');
    }
});