package Service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;

public interface ServiceInterface extends Remote {

    public String recupererDonnees() throws RemoteException;

    
    public String reserverTable(int id, String nom, String prenom, int nbClients, String telephonne, LocalDate date) throws RemoteException;

}