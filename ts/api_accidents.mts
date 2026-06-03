import { PROXY_URL } from "./config.mjs";

export async function getAccidents(){
    try {
        const response = await fetch(PROXY_URL);
        const data = await response.json();
        return data;
    } catch (error) {
        console.error("Erreur lors de la récupération des incidents via le proxy :", error);
        return null;
    }
}


