import { getStationsInfo } from "./api_velib.mjs";
import { addVelibStation} from "./map.mjs";

// On récupère la reponse de l'api
const response = await getStationsInfo();

// On récupère l'ensemble des stations
const stations = response.data.stations;

// Pour chaque station on ajoute le point sur la carte
for (const station of stations) {
    addVelibStation(station);
}