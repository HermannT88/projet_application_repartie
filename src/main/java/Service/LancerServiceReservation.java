package Service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Classe principale chargée de démarrer le service de Réservation RMI.
 */
public class LancerServiceReservation {

    /**
     * Point d'entrée principal du programme serveur pour la réservation.
     *
     * @param args Arguments passés en ligne de commande : args[0] (user DB) et args[1] (password DB).
     */
    public static void main(String args[]) throws RemoteException, ServerNotActiveException {
        if (args.length < 2) {
            System.err.println("Usage: java Service.LancerServiceReservation <user_db> <password_db>");
            System.exit(1);
        }

        try {
            ServiceReservation reserv = new ServiceReservation(args);
            ServiceInterface rd = (ServiceInterface) UnicastRemoteObject.exportObject((Remote) reserv, 0);
            Registry reg;
            try {
                reg = LocateRegistry.createRegistry(1099);
                System.out.println("Annuaire RMI local démarré sur le port 1099.");
            } catch (RemoteException e) {
                reg = LocateRegistry.getRegistry("127.0.0.1", 1099);
                System.out.println("Connexion à l'annuaire RMI existant.");
            }
            reg.rebind("reservation", rd);
            System.out.println("Serveur de réservation prêt et en cours d'exécution...");
            
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
