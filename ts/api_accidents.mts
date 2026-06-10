import { PROXY_URL } from "./config.mjs";
const DIRECT_URL = "http://localhost:8080";

export async function getAccidents(){
    const baseUrls = [DIRECT_URL, PROXY_URL];

    for (const baseUrl of baseUrls) {
        try {
            const response = await fetch(baseUrl + "/incidents");
            
            if (response.ok) {
                const data = await response.json();
                if (data.status) {
                    const waze = JSON.parse(data.message);
                    return { incidents: waze.incidents };
                }
            }
        } catch (error) {
            console.warn(`Échec avec ${baseUrl}, tentative avec l'URL suivante...`);
        }
    }

    console.error("Erreur : Impossible de récupérer les incidents (ni en direct, ni via proxy).");
    return null;
}


