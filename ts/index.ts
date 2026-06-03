import { getStationsInfo } from "./api_velib.mjs";
import { addVelibStation, addIncident, addRestaurant } from "./map.mjs";
import { getAccidents } from "./api_accidents.mjs";
import { getRestaurants } from "./api_restaurants.mjs";

// On récupère la reponse de l'api
const response = await getStationsInfo();

// On récupère l'ensemble des stations
const stations = response.data.stations;

// Pour chaque station on ajoute le point sur la carte
for (const station of stations) {
    addVelibStation(station);
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

// On récupère l'ensemble des accidents 
const resIncidents = await getAccidents();
if (resIncidents && resIncidents.incidents) {
    for (const incident of resIncidents.incidents) {
        addIncident(incident);
    }
} else {
    console.warn("Serveur indisponible.");
}
