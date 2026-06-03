//=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Centralise les actions et configurations relative à la carte
//=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=

declare const L: typeof import('leaflet');

import { Station, StationStatus } from "./config.mjs";
import { getStatus } from "./api_velib.mjs";



//=============================================
// Configuration de la map
//=============================================

var map = L.map('map').setView([48.692054, 6.184417], 12); // Coordonées et zoom par défaut ([Lat,Long],Zoom)

L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map);

//=============================================
// Fonction d'ajout de point cercle etc.. sur la carte
//=============================================

// Ajout point classique
function addPoint(lat: number, long: number): void {
    var marker = L.marker([lat, long]).addTo(map);
}

// Ajout d'un cercle
function addCircle(lat: number, long: number, border: string, fill: string, fillOpacity: number, radius: number): void {
    var circle = L.circle([lat, long], {
        color: border,
        fillColor: fill,
        fillOpacity: fillOpacity,
        radius: radius
    }).addTo(map);
}

// Ajout d'un polygone
function addPolygon(coordonnees: [number, number][], border: string, fill: string, fillOpacity: number): void {
    var polygon = L.polygon(coordonnees, {
        color: border,
        fillColor: fill,
        fillOpacity: fillOpacity
    }).addTo(map);
}

// Ajout d'une station de vélib sur la carte avec sa description
export async function addVelibStation(station: Station) {

    var marker = L.marker([station.lat, station.lon]).addTo(map);

    var status = await getStatus(station);

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
