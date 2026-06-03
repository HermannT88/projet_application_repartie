package Service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface RMI définissant les services de réservation de restaurant disponibles à distance.
 * Étend {@link java.rmi.Remote} pour permettre les invocations de méthodes distantes.
 */
public interface ServiceWazeInterface extends Remote {

    /**
     * Récupère la liste de tous les restaurants de Nancy stockés en base de données.
     * Les données sont retournées sous forme de chaîne formatée en JSON.
     *
     * @return Une chaîne JSON contenant le statut de l'opération (true/false) et la liste des restaurants
     *         sous la forme d'un tableau d'objets (clé "message").
     * @throws RemoteException Si une erreur de communication réseau survient durant l'appel RMI.
     */
    public String recupererDonnees() throws RemoteException;

}