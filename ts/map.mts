//=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Centralise les actions et configurations relative à la carte
//=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=

declare const L: typeof import('leaflet');

import { Station, StationStatus, WazeIncident, Restaurant, VILLES } from "./config.mjs";
import { getStatus } from "./api_velib.mjs";



//=============================================
// Configuration de la map
//=============================================

let map: L.Map;

function getMap(): L.Map {
    if (!map) {
        map = L.map('map').setView([48.692054, 6.184417], 12);
        L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        }).addTo(map);
    }
    return map;
}

//=============================================
// Fonction d'ajout de point cercle etc.. sur la carte
//=============================================

// Ajout d'une station de vélib sur la carte avec sa description
export async function addVelibStation(station: Station, ville: VILLES) {

    const m = getMap();
    var marker = L.marker([station.lat, station.lon]).addTo(m);

    var status = await getStatus(station, ville);

    if (status) {
        var text = "<b>" + station.name + "</b>"
        text += "<br> Adresse : " + station.address;
        text += "<br> Vélo(s) disponible(s) : " + status.num_bikes_available;
        text += "<br> Place(s) disponible(s) : " + status.num_docks_available;
    } else {
        var text = "<b>" + station.name + "</b>"
        text += "<br> Adresse : " + station.address;
        text += "<i> Statut indisponible </i>";
    }
    marker.bindPopup(text);

}

// Ajout d'un incident sur la carte avec sa description
export function addIncident(incident: WazeIncident) {

    const m = getMap();

    if (incident.location && incident.location.polyline) {
        const coords = incident.location.polyline.split(" ");
        const lat = parseFloat(coords[0]);
        const lon = parseFloat(coords[1]);

        var marker = L.marker([lat, lon]).addTo(m);

        const type = incident.short_description || incident.type;
        const rue = incident.location.street || incident.location.location_description;
        const dateFin = new Date(incident.endtime).toLocaleDateString("fr-FR");

        var text = `<b>${type}</b><br>${rue}<br><i>${incident.description}</i><br><small>Jusqu'au ${dateFin}</small>`;
        
        marker.bindPopup(text);
    }
}

// Ajout un restaurant sur la cart avec sa description 
export function addRestaurant(restaurant: Restaurant) {

        const m = getMap();

        const coords = restaurant.coord_GPS.split(", ");
        const lat = parseFloat(coords[0]);
        const lon = parseFloat(coords[1]);

        var marker = L.marker([lat, lon]).addTo(m);

        const nom = restaurant.nom_restaurant;
        const adresse = restaurant.adresse_restaurant;

        var text = `<b>${nom}</b><br>${adresse}<br>`;
        
        marker.bindPopup(text);
}