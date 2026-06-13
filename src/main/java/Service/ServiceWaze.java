package Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.rmi.RemoteException;
import java.time.Duration;

import org.json.JSONObject;

/**
 * Implémentation du service RMI de réservation.
 * Cette classe interroge la base de données Oracle de l'IUT pour
 * récupérer les restaurants et effectuer des réservations de tables de manière
 * sécurisée.
 */
public class ServiceWaze implements ServiceWazeInterface {

    /**
     * Récupère les données de tous les restaurants de la table RESTAURANT de la
     * base Oracle.
     * Charge dynamiquement le driver Oracle JDBC et effectue une requête SELECT.
     *
     * @return Chaîne JSON contenant la liste des restaurants ou un message d'erreur
     *         si la connexion échoue.
     * @throws RemoteException En cas de problème de communication RMI.
     */
    @Override
    public String recupererDonnees() throws RemoteException {
        System.out.println("=> [ServiceWaze] Quelqu'un veut récupérer les données de Waze");

        URI uri = URI.create("https://carto.g-ny.eu/data/cifs/cifs_waze_v2.json");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        // Liste des clients à essayer dans l'ordre
        HttpClient[] clients = {
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .proxy(ProxySelector.of(new InetSocketAddress("www-cache", 3128)))
                        .build(),

                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build()
        };

        // Envoyer la requête et gérer les erreurs
        for (HttpClient client : clients) {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                JSONObject responseJSON = new JSONObject();
                if (response.statusCode() == 200) {
                    responseJSON.put("status", true);
                    responseJSON.put("message", response.body());
                    return responseJSON.toString();
                } else {
                    responseJSON.put("status", false);
                    responseJSON.put("message", "Statut HTTP : " + response.statusCode());
                    return responseJSON.toString();
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Tentative échouée (" + e.getClass().getSimpleName() + ") : " + e.getMessage());
                if (e instanceof InterruptedException)
                    Thread.currentThread().interrupt();
                // continue avec le prochain client
            }
            System.out.println("Envoie des donnees !");
        }
        JSONObject err = new JSONObject();
        err.put("status", false);
        err.put("message", "Impossible de contacter l'API Waze (ni en direct, ni via proxy IUT).");
        return err.toString();
    }
}
