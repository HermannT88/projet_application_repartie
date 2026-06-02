// Fichier de configuration de la carte

/// <reference types="leaflet" />

var map = L.map('map').setView([48.692054, 6.184417], 12); // Coordonées et zoom par défaut ([Lat,Long],Zoom)

L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map);

function addPoint(lat :number, long :number):void{
    var marker = L.marker([lat, long]).addTo(map);
}

function addCircle(lat :number, long :number, border : string, fill : string, fillOpacity : number , radius : number):void {
var circle = L.circle([lat, long], {
    color: border,
    fillColor: fill,
    fillOpacity: fillOpacity,
    radius: radius
}).addTo(map);
}

function addPolygon(coordonnees: [number,number][], border: string, fill: string, fillOpacity: number): void {
    var polygon = L.polygon(coordonnees, {
        color: border,
        fillColor: fill,
        fillOpacity: fillOpacity
    }).addTo(map);
}
