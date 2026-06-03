import { Station, STATION_INFO_URL, STATION_STATUS_URL, StationStatus, VelostanData, VelostanResponse, VelostanStatusResponse } from "./config.mjs";

export async function getStationsInfo(): Promise<VelostanResponse> {
    const response = await fetch(STATION_INFO_URL);
    const data: VelostanResponse = await response.json();
    return data;
}

async function getStationsStatus(): Promise<VelostanStatusResponse> {
    const response = await fetch(STATION_STATUS_URL);
    const data: VelostanStatusResponse = await response.json();
    return data;
}

export async function getStatus(station: Station): Promise<StationStatus | undefined> {

    const statutsData = await getStationsStatus();

    const statut = statutsData.data.stations.find(
        (statusActuel) => statusActuel.station_id === station.station_id
    );

    return statut;
}
