import { PROXY_URL } from "./config.mjs";

export async function getRestaurants(){
    try {
        const response = await fetch(PROXY_URL);
        const data = await response.json();
        return data;
    } catch (error) {
        console.error("Erreur lors de la récupération des restaurants via le proxy :", error);
        return null;
    }
}
