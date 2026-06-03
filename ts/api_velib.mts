import { Station, STATION_INFO_URL, STATION_STATUS_URL, StationStatus, VelostanData, VelostanResponse, VelostanStatusResponse } from "./config.mjs";

// Fonction pour appeler l'API qui donne les information sur les stations
export async function getStationsInfo(): Promise<VelostanResponse> {
    const response = await fetch(STATION_INFO_URL);
    const data: VelostanResponse = await response.json();
    return data;
}

// Fonction pour appeler l'API qui donne les statuts des stations
async function getStationsStatus(): Promise<VelostanStatusResponse> {
    const response = await fetch(STATION_STATUS_URL);
    const data: VelostanStatusResponse = await response.json();
    return data;
}

// Fonction qui prend une station en paramètre et renvoie son statuts
export async function getStatus(station: Station): Promise<StationStatus | undefined> {

    const statutsData = await getStationsStatus();

    const statut = statutsData.data.stations.find(
        (statusActuel) => statusActuel.station_id === station.station_id
    );

    return statut;
}
