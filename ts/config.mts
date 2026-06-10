export const API_BASE = 'https://api.cyclocity.fr/contracts/';

export const STATION_INFO_URL_END = '/gbfs/v2/station_information.json';

export const STATION_STATUS_URL_END = '/gbfs/v2/station_status.json';

export enum VILLES {
  // France
  AMIENS = 'amiens',
  BESANCON = 'besancon',
  LYON = 'lyon',
  MULHOUSE = 'mulhouse',
  NANCY = 'nancy',
  NANTES = 'nantes',
  ROUEN = 'rouen',
  TOULOUSE = 'toulouse',

  // International
  BRUXELLES = 'bruxelles',
  NAMUR = 'namur',
  DUBLIN = 'dublin',
  LJUBLJANA = 'ljubljana',
  LUXEMBOURG = 'luxembourg',
  SANTANDER = 'santander',
  SEVILLE = 'seville',
  VALENCE = 'valence',
  TOYAMA = 'toyama'
}

// Coordonnées du centre de chaque ville
export const VILLES_COORDONEES: Record<VILLES, [number, number]> = {
  [VILLES.AMIENS]:     [49.8942, 2.2958],
  [VILLES.BESANCON]:   [47.2378, 6.0241],
  [VILLES.LYON]:       [45.7640, 4.8357],
  [VILLES.MULHOUSE]:   [47.7508, 7.3359],
  [VILLES.NANCY]:      [48.6921, 6.1844],
  [VILLES.NANTES]:     [47.2184, -1.5536],
  [VILLES.ROUEN]:      [49.4432, 1.0993],
  [VILLES.TOULOUSE]:   [43.6047, 1.4442],

  [VILLES.BRUXELLES]:  [50.8503, 4.3517],
  [VILLES.NAMUR]:      [50.4669, 4.8675],
  [VILLES.DUBLIN]:     [53.3498, -6.2603],
  [VILLES.LJUBLJANA]:  [46.0569, 14.5058],
  [VILLES.LUXEMBOURG]: [49.6116, 6.1319],
  [VILLES.SANTANDER]:  [43.4623, -3.8099],
  [VILLES.SEVILLE]:    [37.3891, -5.9845],
  [VILLES.VALENCE]:    [39.4699, -0.3763],
  [VILLES.TOYAMA]:     [36.6953, 137.2113],
}

// IP de la machine de l'IUT
export const PROXY_URL = "http://IP_MACHINE_PROXY:8080";

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