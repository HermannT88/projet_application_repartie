import { PROXY_URL } from "./config.mjs";
const DIRECT_URL = "http://localhost:8080";

export async function getRestaurants(){
    const baseUrls = [DIRECT_URL, PROXY_URL];

    for (const baseUrl of baseUrls) {
        try {
            console.log("Tentative de connexion à : " + baseUrl + "/restaurants");
            const response = await fetch(baseUrl + "/restaurants");

            if (response.ok) {
                const data = await response.json();
                
                if (data.status) {
                    console.log("Données récupérées avec succès depuis " + baseUrl);
                    return { restaurants: data.message };
                }
            } 
        } catch (error) {
            console.warn(`Échec de la connexion à ${baseUrl}. Raison :`, error);
        }
    }

    console.error("Erreur : Impossible de récupérer les restaurants sur aucune des URLs.");
    return null;
}