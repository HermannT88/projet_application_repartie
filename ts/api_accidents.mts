import { PROXY_URL } from "./config.mjs";

export async function getAccidents(){
    try {
        const response = await fetch(PROXY_URL + "/incidents");
        const data = await response.json();
        if (data.status) {
            const waze = JSON.parse(data.message);
            return { incidents: waze.incidents };
        }
    } catch (error) {
        console.error("Erreur lors de la récupération des incidents via le proxy :", error);
        return null;
    }
}


