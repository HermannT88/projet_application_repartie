package Client;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.ConnectException;
import org.json.JSONObject;
import Service.ServiceWazeInterface;

/**
 * Client RMI simple pour interroger le service Waze distant.
 * Récupère les données d'incidents/trafic via le service Waze
 * enregistré dans l'annuaire RMI et les affiche dans la console.
 *
 * Usage : java Client.ClientWaze <host> <port>
 *   - host : adresse de la machine hébergeant l'annuaire RMI (ex: localhost)
 *   - port : port de l'annuaire RMI (ex: 1099)
 */
public class ClientWaze {

    /**
     * Point d'entrée du client Waze.
     *
     * @param args args[0] = hôte du registre RMI, args[1] = port du registre.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage : java Client.ClientWaze <host> <port>");
            System.out.println("Exemple : java Client.ClientWaze localhost 1099");
            return;
        } 
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            // 1. Connexion à l'annuaire RMI distant
            Registry reg = LocateRegistry.getRegistry(host, port);

            // 2. Récupération du service Waze depuis l'annuaire
            ServiceWazeInterface serviceWaze = (ServiceWazeInterface) reg.lookup("waze");
            System.out.println("Connexion au service Waze réussie.");

            // 3. Appel de la méthode distante
            String resultat = serviceWaze.recupererDonnees();

            // 4. Traitement et affichage du résultat
            if (resultat != null) {
                JSONObject json = new JSONObject(resultat);
                boolean status = json.getBoolean("status");

                if (status) {
                    System.out.println("Données Waze récupérées avec succès :");
                    System.out.println(json.get("message").toString());
                } else {
                    System.out.println("Erreur lors de la récupération (code HTTP : " + json.get("message") + ")");
                }
            } else {
                System.out.println("Aucune réponse du service Waze.");
            }

        } catch (ConnectException e) {
            System.out.println("Connexion impossible avec l'annuaire RMI sur " + host + ":" + port);
        } catch (NotBoundException e) {
            System.out.println("Le service 'waze' est introuvable dans l'annuaire RMI.");
        } catch (RemoteException e) {
            System.out.println("Erreur de communication RMI : " + e.getMessage());
        }
    }
}
