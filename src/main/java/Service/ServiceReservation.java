package Service;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Implémentation du service RMI de réservation.
 * Cette classe interroge la base de données Oracle de l'IUT pour
 * récupérer les restaurants et effectuer des réservations de tables de manière sécurisée.
 */
public class ServiceReservation implements ServiceInterface {

    /**
     * Arguments de ligne de commande reçus au démarrage (contiennent les identifiants DB).
     */
    private String[] args;

    /**
     * Constructeur par défaut initialisant les arguments à un tableau vide.
     */
    public ServiceReservation() {
        this.args = new String[0];
    }

    /**
     * Constructeur initialisant le service avec les arguments contenant les identifiants de connexion Oracle.
     *
     * @param args Les arguments passés à la ligne de commande (nom d'utilisateur et mot de passe DB).
     */
    public ServiceReservation(String[] args) {
        this.args = args != null ? args.clone() : new String[0];
    }

    /**
     * Récupère les données de tous les restaurants de la table RESTAURANT de la base Oracle.
     * Charge dynamiquement le driver Oracle JDBC et effectue une requête SELECT.
     *
     * @return Chaîne JSON contenant la liste des restaurants ou un message d'erreur si la connexion échoue.
     * @throws RemoteException En cas de problème de communication RMI.
     */
    @Override
    public String recupererDonnees() throws RemoteException {
        String url = "jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb";
        JSONArray restaurantsArray = new JSONArray();
        JSONObject response = new JSONObject();

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Driver loaded");
            Connection connection = DriverManager.getConnection(url, args.length >= 2 ? args[0] : "", args.length >= 2 ? args[1] : "");
            System.out.println("Database connected");
            Statement statement = connection.createStatement();

            ResultSet restaurants = statement.executeQuery("select id_restaurant, nom_restaurant, adresse_restaurant, coord_GPS from restaurant");
            while (restaurants.next()) {

                JSONObject reponse = new JSONObject();

                reponse.put("id_restaurant", restaurants.getInt("id_restaurant"));
                reponse.put("nom_restaurant", restaurants.getString("nom_restaurant"));
                reponse.put("adresse_restaurant", restaurants.getString("adresse_restaurant"));
                reponse.put("coord_GPS", restaurants.getString("coord_GPS"));

                restaurantsArray.put(reponse);
            }

            restaurants.close();
            statement.close();
            connection.close();

            response.put("status", true);
            response.put("message", restaurantsArray);
            return response.toString();
        } catch (ClassNotFoundException | SQLException e) {
            response.put("status", false);
            response.put("message", e.getMessage());
            return response.toString();
        }
    }

    /**
     * Effectue une réservation de table dans la base de données Oracle.
     * Utilise une recherche avec verrouillage concurrent (SELECT FOR UPDATE SKIP LOCKED) 
     * puis insère une ligne dans la table RESERVATION.
     *
     * @param idResto    L'identifiant du restaurant ciblé.
     * @param nom        Le nom du client effectuant la réservation.
     * @param prenom     Le prénom du client effectuant la réservation.
     * @param nbClients  Le nombre de convives pour la réservation.
     * @param telephonne Le numéro de téléphone de contact du client.
     * @param date       La date prévue pour la réservation.
     * @return Chaîne JSON indiquant si la réservation a réussi ou échoué.
     * @throws RemoteException En cas de problème de communication RMI.
     */
    @Override
    public String reserverTable(int idResto, String nom, String prenom, int nbClients, String telephonne, LocalDateTime date) throws RemoteException {
        String url = "jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb";
        JSONObject response = new JSONObject();

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            try (Connection connection = DriverManager.getConnection(url, args.length >= 2 ? args[0] : "", args.length >= 2 ? args[1] : "")) {
                connection.setAutoCommit(false);

                // 1. Rechercher une table disponible avec un verrouillage temporaire pour éviter les conflits
                String findTableSql = "SELECT t.id_table FROM table_restau t "
                        + "WHERE t.id_restaurant = ? AND t.capacite >= ? "
                        + "AND NOT EXISTS (SELECT 1 FROM reservation r WHERE r.id_table = t.id_table AND r.dates <= ? AND (r.dates_fin IS NULL OR r.dates_fin >= ?) ) "
                        + "FOR UPDATE SKIP LOCKED";

                int idTable = -1;
                try (PreparedStatement ps = connection.prepareStatement(findTableSql)) {
                    ps.setInt(1, idResto);
                    ps.setInt(2, nbClients);
                    ps.setTimestamp(3, Timestamp.valueOf(date));
                    ps.setTimestamp(4, Timestamp.valueOf(date));

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            idTable = rs.getInt("id_table");
                        }
                    }
                }

                // 2. Si une table a été trouvée, procéder à l'insertion
                if (idTable != -1) {
                    String insertSql = "INSERT INTO reservation (id_res, id_table, nom_client, prenom_client, nb_convives, telephone, dates, dates_fin, montant) "
                            + "VALUES (seq_restaurant.NEXTVAL, ?, ?, ?, ?, ?, ?, NULL, 0)";
                    
                    try (PreparedStatement ins = connection.prepareStatement(insertSql)) {
                        ins.setInt(1, idTable);
                        ins.setString(2, nom);
                        ins.setString(3, prenom);
                        ins.setInt(4, nbClients);
                        ins.setString(5, telephonne);
                        ins.setTimestamp(6,Timestamp.valueOf(date));

                        int updated = ins.executeUpdate();
                        connection.commit();

                        if (updated == 1) {
                            response.put("status", true);
                            response.put("message", "Réservation confirmée pour la table " + idTable);
                        } else {
                            response.put("status", false);
                            response.put("message", "Échec de l'insertion de la réservation en base de données.");
                        }
                    }
                } else {
                    // Aucune table trouvée
                    connection.rollback();
                    response.put("status", false);
                    response.put("message", "Aucune table disponible pour ce restaurant à cette date.");
                }
            }
            return response.toString();
        } catch (ClassNotFoundException | SQLException e) {
            response.put("status", false);
            response.put("message", "Erreur de réservation : " + e.getMessage());
            return response.toString();
        }
    }
}

