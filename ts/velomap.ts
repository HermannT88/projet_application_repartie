import { getStationsInfo } from "./api_velib.mjs";
import { VILLES, VILLES_COORDONEES } from "./config.mjs";
import { addVelibStation, centerMap, clearMap } from "./map.mjs";

async function loadVille(ville: VILLES) {
    clearMap();
    const [lat, lon] = VILLES_COORDONEES[ville];
    centerMap(lat, lon);
    const response = await getStationsInfo(ville);
    for (const station of response.data.stations) {
        addVelibStation(station, ville);
    }
}

loadVille(VILLES.NANCY);

document.addEventListener('click', (e) => {
    const card = (e.target as HTMLElement).closest('.city-card');
    if (!card) return;
    loadVille(card.id as VILLES);
});