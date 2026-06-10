//=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Centralise les actions et configurations relative à la carte
//=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=

declare const L: typeof import('leaflet');

import { Station, StationStatus, WazeIncident, Restaurant, VILLES } from "./config.mjs";
import { getStatus } from "./api_velib.mjs";
import Handlebars from "handlebars";
import { getReservations, getRestaurants, reserverTable } from "./api_restaurants.mjs";
let template = document.getElementById("reservationModal");


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

// Liste des markers sur la carte
let markers: L.Marker[] = [];

// Ajout d'une station de vélib sur la carte avec sa description
export async function addVelibStation(station: Station, ville: VILLES) {

    const m = getMap();
    var marker = L.marker([station.lat, station.lon]).addTo(m);
    markers.push(marker);

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
        markers.push(marker);

        const type = incident.short_description || incident.type;
        const rue = incident.location.street || incident.location.location_description;
        const dateFin = new Date(incident.endtime).toLocaleDateString("fr-FR");

        var text = `<b>${type}</b><br>${rue}<br><i>${incident.description}</i><br><small>Jusqu'au ${dateFin}</small>`;

        marker.bindPopup(text);
    }
}

// Ajout un restaurant sur la carte avec sa description 
export function addRestaurant(restaurant: Restaurant) {

    const m = getMap();

    const coords = restaurant.coord_GPS.split(",");
    const lat = parseFloat(coords[0]);
    const lon = parseFloat(coords[1]);

    var marker = L.marker([lat, lon]).addTo(m);

    const nom = restaurant.nom_restaurant;
    const adresse = restaurant.adresse_restaurant;
    const popupContent = document.createElement("div");

    // Création de la pop up rapide 

    popupContent.innerHTML = `
    <b>${restaurant.nom_restaurant}</b><br>
    ${restaurant.adresse_restaurant}<br><br>
  `;

    const btn = document.createElement("button");
    btn.textContent = "Réserver";
    btn.style.cssText = `
    margin-top:6px;
    padding:5px 10px;
    background:#e74c3c;
    color:white;
    border:none;
    border-radius:4px;
    cursor:pointer;
  `;

    btn.addEventListener("click", () => openReservation(restaurant.id_restaurant, restaurant.nom_restaurant));
    popupContent.appendChild(btn);
    marker.bindPopup(popupContent);

}



//=============================================
// Gestion de la réservation
//=============================================



// Pop up de réservation de restaurant

let currentRestaurantId: number | null = null;

// Initialisation de la pop up

export function initReservationModal() {

    const confirmBtn = document.getElementById("confirmRes")!;
    const cancelBtn = document.getElementById("cancelRes")!;

    confirmBtn.addEventListener("click", submitReservation);
    cancelBtn.addEventListener("click", closeReservation);

}

// ouverture de celle-ci

export function openReservation(id: number, nomResto: string) {

    initReservationModal();

    currentRestaurantId = id;

    const modal = document.getElementById("reservationModal");

    // Equivalent de handlebars

    const title = modal?.querySelector("h2");
    if (title) {
        title.textContent = nomResto
    };

    modal?.classList.remove("hidden");
}

export function closeReservation() {
    const modal = document.getElementById("reservationModal") as HTMLElement;
    modal.classList.add("hidden");

    // Vider les champs
    (document.getElementById("resName") as HTMLInputElement).value = "";
    (document.getElementById("resPrenom") as HTMLInputElement).value = "";
    (document.getElementById("resPeople") as HTMLInputElement).value = "";
    (document.getElementById("resTel") as HTMLInputElement).value = "";
    (document.getElementById("resDate") as HTMLInputElement).value = "";
}

// Envoyer le contenu du formulaire

async function submitReservation() {

    const nom = (document.getElementById("resName") as HTMLInputElement).value;
    const prenom = (document.getElementById("resPrenom") as HTMLInputElement).value;
    const clients = (document.getElementById("resPeople") as HTMLInputElement).value;
    const date = (document.getElementById("resDate") as HTMLInputElement).value;
    const tel = (document.getElementById("resTel") as HTMLInputElement).value;

    if (!currentRestaurantId) return;

    const result = await reserverTable(
        currentRestaurantId,
        nom,
        prenom,
        parseInt(clients),
        tel,
        date
    );

    if (result === null) {
        alert("Serveur injoignable.");
    } else {
        alert(result.message);
    }
    closeReservation();

    const dropdown = document.getElementById("restoSelect") as HTMLSelectElement;
    await afficherReservations(parseInt(dropdown.value));
}


async function afficherReservation(idResto?: number) {
    if (idResto) {

    }
}

// Initialisation de la sidebar pour voir les réservations

export async function initSidebar() {
    const dropdown = document.getElementById("restoSelect") as HTMLSelectElement;

    // Remplir la dropdown
    const data = await getRestaurants();
    data.restaurants.forEach(resto => {
        const option = document.createElement("option");
        // ON met en option value l'id du resto et on affiche le nom du restaurant
        option.value = String(resto.id_restaurant);
        option.textContent = resto.nom_restaurant;
        dropdown.appendChild(option);
    });

    // Charger toutes les réservations au départ
    await afficherReservations(-1);

    // Recharger quand on change de restaurant
    dropdown.addEventListener("change", async () => {
        await afficherReservations(parseInt(dropdown.value));
    });
}

async function afficherReservations(idResto: number) {
    const liste = document.getElementById("listeReservation")!;
    liste.innerHTML = "<li>Chargement...</li>";

    const result = await getReservations(idResto);
    liste.innerHTML = "";

    if (result.status && result.message.length > 0) {
        result.message.forEach((res: any) => {
            const li = document.createElement("li");
            li.innerHTML = `<b>${res.nom_client}</b><br>
                            Table ${res.id_table} — ${res.nb_convives} pers.<br>
                            ${res.debut} → ${res.fin}`;
            liste.appendChild(li);
        });
    } else {
        liste.innerHTML = "<li>Aucune réservation.</li>";
    }
}

// Méthode pour vider la carte
export function clearMap() {
    for (const marker of markers) {
        marker.remove();
    }
    markers = [];
}

// Méthode pour centrer la mpa sur des coordonées
export function centerMap(lat: number, lon: number) {
    const m = getMap();
    m.setView([lat, lon], 12);

}

// Méthode pour ouvrir la sidebar
document.addEventListener("DOMContentLoaded", () => {
    initSidebar();
});