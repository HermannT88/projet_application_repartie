package src.main.java.Client;

//On fait les imports nessessaire pour utiliser RMI
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.ConnectException;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import src.main.java.Service.ServiceInterface;

/**
 * Classe client pour l'application RMI de réservation.
 * Cette classe permet à l'utilisateur d'interagir en ligne de commande (CLI)
 * avec le service distant pour récupérer les restaurants ou réserver une table.
 */
public class Client {

    /**
     * Affiche de manière lisible dans la console la liste des restaurants
     * fournie sous forme d'un JSONArray.
     *
     * @param resJson Le tableau JSON contenant les informations des restaurants.
     */
	public void affichage(JSONArray resJson){
		System.out.println("Restaurants :");

		for(int i = 0; i < resJson.length(); i++){
			JSONObject restau = resJson.getJSONObject(i);

			int id = restau.getInt("id_restaurant");
			String nom = restau.getString("nom_restaurant");
			String adresse = restau.getString("adresse_restaurant");
			String coordonnees = restau.getString("coord_GPS");

			System.out.println(id + " - " + nom + adresse + "(" + coordonnees + ")");

		}

		System.out.println("Fin de l'affichage des restaurants.");
	}

    /**
     * Point d'entrée du programme client.
     * Initialise la connexion à l'annuaire RMI et affiche un menu interactif.
     *
     * @param args Arguments en ligne de commande : args[0] (hôte du registre RMI) et args[1] (port du registre).
     * @throws RemoteException En cas d'erreur réseau RMI.
     * @throws NotBoundException Si le service RMI recherché n'est pas lié dans l'annuaire.
     */
    public static void main(String[] args)throws RemoteException, NotBoundException {
	// Scanner
		Scanner sc = new Scanner(System.in);
	
	//On récupère les paramètres d'entrée dans des variables
	//Adresse ip de la machine qui héberge le service, "localhost" si c'est sur notre machine
        String host = args[0];
        
        //Le port de l'annuaire distant, en principe 1099
        int port = Integer.parseInt(args[1]);
	
	try{
	//On récupère l'annuaire distant avec les paramètres d'entrée
        Registry reg = LocateRegistry.getRegistry(host, port);
	
	//Recupérer le service dans l'annuaire et le cast avec l'interface
        ServiceInterface reservation = (ServiceInterface) reg.lookup("reservation");
        
	// Choix utilisateur
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		boolean exit = false;
		while(!exit){
			System.out.println("Que voulez-vous faire :");
			System.out.println("1 - Récupérer les données");
			System.out.println("2 - Faire une réservation");
			System.out.println("Autre - Quitter");
			Client client = new Client();
			int choix = sc.nextInt();
			sc.nextLine();

			switch(choix){
				case 1 :
                    String resJson = reservation.recupererDonnees();
                    JSONObject rootJson = new JSONObject(resJson);
                    if (rootJson.has("message")) {
                        Object message = rootJson.get("message");
                        if (message instanceof JSONArray) {
                            client.affichage((JSONArray) message);
                        } else {
                            System.out.println("Erreur du service : " + message.toString());
                        }
                    } else {
                        System.out.println("Réponse invalide du service : " + resJson);
                    }
                    break;
				case 2:
						System.out.print("Numéro du restaurant : ");
                        int id_restau = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Date de la réservation (dd/MM/yyyy) : ");
                        String saisie = sc.nextLine();
                        LocalDate date = null;
                        
                        try {
                            date = LocalDate.parse(saisie, format);
                        } catch (DateTimeParseException e) {
                            System.out.println("Date invalide. Annulation de la réservation.\n");
                            break;
						}

                        System.out.print("Nom pour la réservation : ");
                        String nom = sc.nextLine();

                        System.out.print("Prénom pour la réservation : ");
                        String prenom = sc.nextLine();

                        System.out.print("Nombre de convives : ");
                        int convives = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Téléphone : ");
                        String tel = sc.nextLine();

                        // Appel RMI
                        String reponseJson = reservation.reserverTable(id_restau, nom, prenom, convives, tel, date);
												System.out.println(reponseJson);

					break;
				default : 
					exit = true;
					break;
			}
		}

	}catch(ConnectException c){	
		System.out.println("La connexion impossible avec l'annuaire");
	}catch(NotBoundException n){
		System.out.println("Le service est introuvable/ne fonctionne pas");
	}
    }
}
