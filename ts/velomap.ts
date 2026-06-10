import { getStationsInfo } from "./api_velib.mjs";
import { VILLES } from "./config.mjs";
import { addVelibStation, clearMap } from "./map.mjs";

async function loadVille(ville: VILLES) {
    clearMap();
    const response = await getStationsInfo(ville);
    const stations = response.data.stations;
    for (const station of stations) {
        addVelibStation(station, ville);
    }
}

// Chargement initial
loadVille(VILLES.NANCY);

// Écoute du sélecteur
const select = document.getElementById("ville-select") as HTMLSelectElement;
select.addEventListener("change", () => {
    const ville = select.value as VILLES;
    loadVille(ville);
});