import { PROXY_URL, RetourReservations, RetourRestaurant } from "./config.mjs";
const DIRECT_URL = "http://localhost:8080";

export async function getRestaurants(){
    const baseUrls = [DIRECT_URL, PROXY_URL];

    for (const baseUrl of baseUrls) {
        try {
            console.log("Tentative de connexion à : " + baseUrl + "/restaurants");
            const response = await fetch(baseUrl + "/restaurants");

            if (response.ok) {
                const data : RetourRestaurant = await response.json();
                
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
    throw new Error;
    
}

export async function reserverTable(idResto: number,nom: string,prenom: string,nbClients: number,telephone: string,date: string): Promise<{ status: boolean; message: string }> {

    const baseUrls = [DIRECT_URL, PROXY_URL];

    for (const baseUrl of baseUrls) {
        try {
            console.log("Tentative de réservation via : " + baseUrl + "/reserver");

            const response = await fetch(baseUrl + "/reserver", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ idResto, nom, prenom, nbClients, telephone, date })
            });

            if (response.ok) {
                const data = await response.json();
                console.log("Réponse reçue depuis " + baseUrl);
                return { status: data.status, message: data.message };
            }

        } catch (error) {
            console.warn(`Échec de la connexion à ${baseUrl}. Raison :`, error);
        }
    }

    console.error("Erreur : Impossible d'effectuer la réservation sur aucune des URLs.");
    throw new Error;
}

export async function getReservations(idResto: number): Promise<RetourReservations> {
    const baseUrls = [DIRECT_URL, PROXY_URL];
    for (const baseUrl of baseUrls) {
        try {
            const response = await fetch(baseUrl + "/reservations", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ idResto })
            });
            if (response.ok) {
                const data : RetourReservations = await response.json();
                return data;
            }
        } catch (error) {
            console.warn(`Échec de la connexion à ${baseUrl}. Raison :`, error);
        }
    }
    throw new Error("Impossible de récupérer les réservations.");
}