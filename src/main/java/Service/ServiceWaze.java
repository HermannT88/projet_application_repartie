package Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.rmi.RemoteException;

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
    // 1. Configurer le client AVEC le proxy de l'IUT
    
    // HttpClient client = HttpClient.newHttpClient();

    //ou si c'est une machine de l'IUT
  
       HttpClient client = HttpClient.newBuilder()
          .proxy(ProxySelector.of(new InetSocketAddress("www-cache", 3128)))
          .build();
  
    // 2. Préparer la requête vers l'URL des données

  

        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://carto.g-ny.eu/data/cifs/cifs_waze_v2.json"))
        .GET()
        .build();
    

    // 3. Envoyer la requête et gérer les erreurs
    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      JSONObject responseJSON = new JSONObject();

      if (response.statusCode() == 200) {
        responseJSON.put("status", true);
        responseJSON.put("message", response.body());
        return responseJSON.toString();
      } else {
        responseJSON.put("status", false);
        responseJSON.put("message", response.statusCode());
        return responseJSON.toString();
      }
    } catch (IOException | InterruptedException e) {
      // 1. On avertit l'utilisateur avec un message clair
      System.err.println("Erreur : Impossible de récupérer les données des restaurants.");

      System.err.println("Détail technique : " + e.getMessage());

      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt(); // Restaure le statut d'interruption
      }
    }
    return null;
  }
}
