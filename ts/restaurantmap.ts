import { getRestaurants } from "./api_restaurants.mjs";
import {addRestaurant } from "./map.mjs";

const toggle = document.getElementById("sidebar-toggle") as HTMLDivElement | null;
const sidebar = document.getElementById("sidebar") as HTMLDivElement | null;

if (toggle && sidebar) {
    toggle.addEventListener("click", () => {
        sidebar.classList.toggle("open");

        toggle.textContent = sidebar.classList.contains("open") ? "❯" : "❮" ;
    });
}


// On récupère l'ensemble des restaurants 
const resRestaurant = await getRestaurants();
if (resRestaurant && resRestaurant.restaurants) {
    for (const restau of resRestaurant.restaurants) {
        addRestaurant(restau);
    }
} else {
    console.warn("Serveur indisponible.");
}
