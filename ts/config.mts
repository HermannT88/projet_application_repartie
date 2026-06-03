export const STATION_INFO_URL = 'https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_information.json';

export const STATION_STATUS_URL = 'https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_status.json';

// IP de la machine de l'IUT
export const PROXY_URL ="";

//=============================================
// Interfaces pour cast les retours JSON
//=============================================

// ** Interfaces des infos des stations ** 

// Interface des stations
export interface Station {
  station_id: string;
  name: string;
  lat: number;
  lon: number;
  address: string;
  rental_methods?: string[]; //Optionel => pas sur tout les retours
  capacity: number;
}

// Interface de la liste des stations
export interface VelostanData {
  stations: Station[];
}

// Interface de la réponse 
export interface VelostanResponse {
  last_updated: number;
  ttl: number;
  version: string;
  data: VelostanData;
}

// ** Interfaces des statuts des stations ** 

// Interface du type de véhicule disponible
export interface VehicleType {
  vehicle_type_id: string;
  count: number;
}

//  Etat d'une station en temps réel
export interface StationStatus {
  station_id: string;
  num_bikes_available: number;
  vehicle_types_available: VehicleType[];
  num_bikes_disabled: number;
  num_docks_available: number;
  num_docks_disabled: number;
  is_installed: boolean;
  is_renting: boolean;
  is_returning: boolean;
  last_reported: number;
}

// Interface du tableau des états des stations 
export interface VelostanStatusData {
  stations: StationStatus[];
}

// Interface de la réponse globale
export interface VelostanStatusResponse {
  last_updated: number;
  ttl: number;
  version: string;
  data: VelostanStatusData;
}

// Interface d'un incident 
export interface WazeIncident {
  type: string;
  description: string;
  short_description: string;
  starttime: string;
  endtime: string;
  location: WazeLocation;
  updatetime: string;
  creationTime: string;
}

// Localisation d'un incident
export interface WazeLocation {
    street: string;
    polyline: string; // latitude et longitude
    location_description: string;
}

// Provenance de l'information sur l'incident 
export interface WazeSource {
  name: string;
  reference: string;
}

// Tableau de la réponse globale
export interface WazeResponse {
    incidents: WazeIncident[];
}

// Interface pour le restaurant
export interface Restaurant {
    id_restaurant: number;
    nom_restaurant: string;
    adresse_restaurant: string;
    coord_GPS: string;
}

// Tableau de la réponse globale
export interface RestaurantResponse {
  restaurants: Restaurant[];
}