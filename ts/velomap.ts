import { getStationsInfo } from "./api_velib.mjs";
import { VILLES, VILLES_COORDONEES } from "./config.mjs";
import { addVelibStation, centerMap, clearMap } from "./map.mjs";
import './component/CityCard'; 

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
    const card = (e.target as HTMLElement).closest('.city-card') as HTMLElement;
    if (!card) return;

    document.querySelectorAll('.city-card').forEach(c => c.classList.remove('active'));
    card.classList.add('active');

    loadVille(card.id as VILLES);
});