import { Station, API_BASE, STATION_INFO_URL_END, STATION_STATUS_URL_END, VILLES, StationStatus, VelostanData, VelostanResponse, VelostanStatusResponse } from "./config.mjs";

// Fonction pour appeler l'API qui donne les information sur les stations
export async function getStationsInfo(ville : VILLES): Promise<VelostanResponse> {
    const URL = API_BASE + ville + STATION_INFO_URL_END;
    const response = await fetch(URL);
    const data: VelostanResponse = await response.json();
    return data;
}

// Fonction pour appeler l'API qui donne les statuts des stations
async function getStationsStatus(ville : VILLES): Promise<VelostanStatusResponse> {
    const URL = API_BASE + ville + STATION_STATUS_URL_END;
    const response = await fetch(URL);
    const data: VelostanStatusResponse = await response.json();
    return data;
}

// Fonction qui prend une station en paramètre et renvoie son statuts
export async function getStatus(station : Station, ville : VILLES): Promise<StationStatus | undefined> {

    const statutsData = await getStationsStatus(ville);

    const statut = statutsData.data.stations.find(
        (statusActuel) => statusActuel.station_id === station.station_id
    );

    return statut;
}
