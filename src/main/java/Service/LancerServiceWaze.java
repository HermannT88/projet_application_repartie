package Service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Classe principale chargée de démarrer le service Waze RMI.
 */
public class LancerServiceWaze {

    /**
     * Point d'entrée principal du programme serveur pour Waze.
     */
    public static void main(String args[]) throws RemoteException, ServerNotActiveException {
        try {
            ServiceWaze waze = new ServiceWaze();
            ServiceWazeInterface rd2 = (ServiceWazeInterface) UnicastRemoteObject.exportObject((Remote) waze, 0);
            Registry reg;
            try {
                reg = LocateRegistry.createRegistry(1099);
                System.out.println("Annuaire RMI local démarré sur le port 1099.");
            } catch (RemoteException e) {
                reg = LocateRegistry.getRegistry("127.0.0.1", 1099);
                System.out.println("Connexion à l'annuaire RMI existant.");
            }
            reg.rebind("waze", rd2);
            System.out.println("Serveur de waze prêt et en cours d'exécution...");
            
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
