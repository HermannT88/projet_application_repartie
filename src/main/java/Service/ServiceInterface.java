package Service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

/**
 * Interface RMI définissant les services de réservation de restaurant disponibles à distance.
 * Étend {@link java.rmi.Remote} pour permettre les invocations de méthodes distantes.
 */
public interface ServiceInterface extends Remote {

    /**
     * Récupère la liste de tous les restaurants de Nancy stockés en base de données.
     * Les données sont retournées sous forme de chaîne formatée en JSON.
     *
     * @return Une chaîne JSON contenant le statut de l'opération (true/false) et la liste des restaurants
     *         sous la forme d'un tableau d'objets (clé "message").
     * @throws RemoteException Si une erreur de communication réseau survient durant l'appel RMI.
     */
    public String recupererDonnees() throws RemoteException;

    /**
     * Réserve une table libre dans un restaurant spécifique pour une date donnée.
     * Cette méthode recherche une table disponible ayant une capacité suffisante,
     * puis enregistre la réservation si une table est trouvée.
     *
     * @param id          L'identifiant du restaurant ciblé.
     * @param nom         Le nom du client effectuant la réservation.
     * @param prenom      Le prénom du client effectuant la réservation.
     * @param nbClients   Le nombre de convives pour la réservation.
     * @param telephonne  Le numéro de téléphone de contact du client.
     * @param date        La date prévue pour la réservation (format LocalDate).
     * @return Une chaîne JSON contenant le statut (true/false) et un message indiquant le succès
     *         (avec le numéro de table attribué) ou l'échec de la réservation.
     * @throws RemoteException Si une erreur de communication réseau survient durant l'appel RMI.
     */
    public String reserverTable(int id, String nom, String prenom, int nbClients, String telephonne, LocalDateTime date) throws RemoteException;

}