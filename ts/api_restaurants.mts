import { PROXY_URL } from "./config.mjs";

export async function getRestaurants(){
    try {
        const response = await fetch(PROXY_URL + "/restaurants");
        const data = await response.json();
        if (data.status) return { restaurants: JSON.parse(data.message) };
    } catch (error) {
        console.error("Erreur lors de la récupération des restaurants via le proxy :", error);
        return null;
    }
}
