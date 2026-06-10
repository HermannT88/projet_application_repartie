import './component/NavBar';

const toggle = document.getElementById("sidebar-toggle") as HTMLDivElement | null;
const sidebar = document.getElementById("sidebar") as HTMLDivElement | null;

if (toggle && sidebar) {
    toggle.addEventListener("click", () => {
        sidebar.classList.toggle("open");

        toggle.textContent = sidebar.classList.contains("open") ? "❯" : "❮" ;
    });
}
