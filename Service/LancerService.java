package Service;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ServerNotActiveException;

public class LancerService {
    public static void main(String args[]) throws RemoteException,
            ServerNotActiveException {
        try {
            ServiceReservation reserv = new ServiceReservation(args); // creation d un objet (new)
            ServiceInterface rd = (ServiceInterface) UnicastRemoteObject.exportObject((Remote) reserv, 0); // 0 pour que OS donne le
            // port automatiquement
            Registry reg = LocateRegistry.getRegistry(1099); // 1099 port par defaut des annuaire (modifiable)
            reg.rebind("reservation", rd); // donner un nom dans l annuaire pour le service
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
    }
}