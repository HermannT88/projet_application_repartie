import { getAccidents } from "./api_accidents.mjs";
import {addIncident} from "./map.mjs";

// On récupère l'ensemble des accidents 
const resIncidents = await getAccidents();
if (resIncidents && resIncidents.incidents) {
    for (const incident of resIncidents.incidents) {
        addIncident(incident);
    }
} else {
    console.warn("Serveur indisponible.");
}
