package Service;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ServerNotActiveException;

/**
 * Classe principale chargée de démarrer le service RMI.
 * Elle instancie l'objet distant, l'exporte sur un port dynamique
 * et l'enregistre dans l'annuaire RMI local (sur le port 1099).
 */
public class LancerService {

    /**
     * Point d'entrée principal du programme serveur.
     * Récupère l'annuaire RMI sur 127.0.0.1 et y lie le service sous le nom "reservation".
     *
     * @param args Arguments passés en ligne de commande : args[0] (user DB) et args[1] (password DB).
     * @throws RemoteException Si un problème de communication RMI survient.
     * @throws ServerNotActiveException Si le serveur RMI n'est pas actif lors de l'appel.
     */
    public static void main(String args[]) throws RemoteException,
            ServerNotActiveException {
        try {
            ServiceReservation reserv = new ServiceReservation(args); // creation d un objet (new)
            ServiceInterface rd = (ServiceInterface) UnicastRemoteObject.exportObject((Remote) reserv, 0); // 0 pour que OS donne le
            // port automatiquement
            Registry reg;
            try {
                // Tente de créer l'annuaire RMI localement sur le port 1099
                reg = LocateRegistry.createRegistry(1099);
                System.out.println("Annuaire RMI local démarré sur le port 1099.");
            } catch (RemoteException e) {
                // Si l'annuaire est déjà lancé (ex: par rmiregistry séparé)
                reg = LocateRegistry.getRegistry("127.0.0.1", 1099);
                System.out.println("Connexion à l'annuaire RMI existant.");
            }
            reg.rebind("reservation", rd); // donner un nom dans l annuaire pour le service
            System.out.println("Serveur de réservation prêt et en cours d'exécution...");
            
            // Bloque le thread principal pour empêcher l'arrêt du processus (notamment avec Maven exec:java)
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}