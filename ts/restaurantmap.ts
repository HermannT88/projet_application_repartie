import { getRestaurants } from "./api_restaurants.mjs";
import {addRestaurant } from "./map.mjs";

// On récupère l'ensemble des restaurants 
const resRestaurant = await getRestaurants();
if (resRestaurant && resRestaurant.restaurants) {
    for (const restau of resRestaurant.restaurants) {
        addRestaurant(restau);
    }
} else {
    console.warn("Serveur indisponible.");
}
