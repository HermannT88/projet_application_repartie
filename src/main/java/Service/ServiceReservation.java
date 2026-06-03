package Service;

import java.io.IOException;
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
 * Implémentation du service RMI de réservation. Cette classe interroge la base
 * de données Oracle de l'IUT pour récupérer les restaurants et effectuer des
 * réservations de tables de manière sécurisée.
 */
public class ServiceReservation implements ServiceInterface {

    /**
     * Arguments de ligne de commande reçus au démarrage (contiennent les
     * identifiants DB).
     */
    private String[] args;

    /**
     * Constructeur par défaut initialisant les arguments à un tableau vide.
     */
    public ServiceReservation() throws IOException {

        try {
            this.args = new String[0];
            JSONArray restaurants = this.recupererRestaurant();
            this.construireBd(restaurants);
        } catch (SQLException | ClassNotFoundException | IOException e) {
            System.err.println("Connexion impossible");
        }

    }

    /**
     * Constructeur initialisant le service avec les arguments contenant les
     * identifiants de connexion Oracle.
     *
     * @param args Les arguments passés à la ligne de commande (nom
     * d'utilisateur et mot de passe DB).
     */
    public ServiceReservation(String[] args) {
        this.args = args != null ? args.clone() : new String[0];
        try {
            JSONArray restaurants = this.recupererRestaurant();
            this.construireBd(restaurants);
        } catch (SQLException | ClassNotFoundException | IOException e) {
            System.err.println("Connexion impossible");
        }
    }

    /**
     * Récupère les données de tous les restaurants de la table RESTAURANT de la
     * base Oracle. Charge dynamiquement le driver Oracle JDBC et effectue une
     * requête SELECT.
     *
     * @return Chaîne JSON contenant la liste des restaurants ou un message
     * d'erreur si la connexion échoue.
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
     * Effectue une réservation de table dans la base de données Oracle. Utilise
     * une recherche avec verrouillage concurrent (SELECT FOR UPDATE SKIP
     * LOCKED) puis insère une ligne dans la table RESERVATION.
     *
     * @param idResto L'identifiant du restaurant ciblé.
     * @param nom Le nom du client effectuant la réservation.
     * @param prenom Le prénom du client effectuant la réservation.
     * @param nbClients Le nombre de convives pour la réservation.
     * @param telephonne Le numéro de téléphone de contact du client.
     * @param date La date prévue pour la réservation.
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
                        ins.setTimestamp(6, Timestamp.valueOf(date));

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

    private JSONArray recupererRestaurant() throws IOException {

        // Lecture du fichier GeoJSON depuis le système de fichiers
        String contenu = java.nio.file.Files.readString(java.nio.file.Path.of("export.geojson"));

        // Parse du GeoJSON complet
        JSONObject geojson = new JSONObject(contenu);

        // Récupération du tableau "features" qui contient tous les restaurants
        JSONArray features = geojson.getJSONArray("features");

        // Construction d'un tableau avec les infos de chaque restaurant
        JSONArray restaurants = new JSONArray();

        for (int i = 0; i < features.length(); i++) {
            JSONObject feature = features.getJSONObject(i);
            JSONObject properties = feature.getJSONObject("properties");
            JSONObject geometry = feature.getJSONObject("geometry");
            JSONArray coordinates = geometry.getJSONArray("coordinates");

            JSONObject restaurant = new JSONObject();
            restaurant.put("id", feature.optString("id", ""));
            restaurant.put("nom", properties.optString("name", "Sans nom"));
            restaurant.put("longitude", coordinates.getDouble(0));
            restaurant.put("latitude", coordinates.getDouble(1));

            // Ajout des propriétés optionnelles si présentes
            if (properties.has("opening_hours")) {
                restaurant.put("horaires", properties.getString("opening_hours"));
            }
            if (properties.has("website") || properties.has("contact:website")) {
                restaurant.put("website", properties.optString("website", properties.optString("contact:website", "")));
            }

            restaurants.put(restaurant);
        }

        return restaurants;
    }

    private void construireBd(JSONArray restaurants) throws SQLException, ClassNotFoundException {
        String url = "jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb";

        // Connexion à la bd
        Class.forName("oracle.jdbc.driver.OracleDriver");
        System.out.println("Driver loaded");
        Connection connection = DriverManager.getConnection(url, args.length >= 2 ? args[0] : "", args.length >= 2 ? args[1] : "");
        System.out.println("Database connected");

        try {
            resetRestaurantDatabase(connection);
            createBD(connection);
            insererRestaurants(connection, restaurants);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void resetRestaurantDatabase(Connection connection) throws SQLException {

        try (Statement st = connection.createStatement()) {
            st.executeUpdate("DROP TABLE plat CASCADE CONSTRAINTS");
        } catch (SQLException e) {
            System.out.println("plat n'existe pas");
        }

        try (Statement st = connection.createStatement()) {
            st.executeUpdate("DROP TABLE reservation CASCADE CONSTRAINTS");
        } catch (SQLException e) {
            System.out.println("reservation n'existe pas");
        }

        try (Statement st = connection.createStatement()) {
            st.executeUpdate("DROP TABLE table_restau CASCADE CONSTRAINTS");
        } catch (SQLException e) {
            System.out.println("table_restau n'existe pas");
        }

        try (Statement st = connection.createStatement()) {
            st.executeUpdate("DROP TABLE restaurant CASCADE CONSTRAINTS");
        } catch (SQLException e) {
            System.out.println("restaurant n'existe pas");
        }

        try (Statement st = connection.createStatement()) {
            st.executeUpdate("DROP SEQUENCE seq_restaurant");
        } catch (SQLException e) {
            System.out.println("Sequence inexistante, ignorée");
        }
    }

    private void createBD(Connection connection) throws SQLException {

        Statement st = connection.createStatement();

        st.executeUpdate("""
        CREATE TABLE restaurant (
            id_restaurant NUMBER(4),
            nom_restaurant VARCHAR2(50),
            adresse_restaurant VARCHAR2(80),
            coord_gps VARCHAR2(50),
            PRIMARY KEY(id_restaurant)
        )
    """);

        st.executeUpdate("""
        CREATE TABLE table_restau (
            id_table NUMBER(4),
            id_restaurant NUMBER(4),
            capacite NUMBER(2),
            PRIMARY KEY(id_table),
            CONSTRAINT fk_table_restau
            FOREIGN KEY (id_restaurant)
            REFERENCES restaurant(id_restaurant)
        )
    """);

        st.executeUpdate("""
        CREATE TABLE reservation (
            id_res NUMBER(4),
            id_table NUMBER(4),
            nom_client VARCHAR2(40),
            prenom_client VARCHAR2(40),
            nb_convives NUMBER(2),
            telephone VARCHAR2(15),
            dates DATE,
            dates_fin DATE,
            montant NUMBER(3),
            PRIMARY KEY(id_res),
            CONSTRAINT fk_res_table FOREIGN KEY (id_table)
            REFERENCES table_restau(id_table)
        )
    """);

        st.executeUpdate("""
        CREATE TABLE plat (
            id_plat NUMBER(4),
            id_res NUMBER(4),
            libelle_plat VARCHAR2(40),
            prix_unitaire NUMBER(2),
            quantite_stockee NUMBER(4),
            PRIMARY KEY(id_plat),
            CONSTRAINT fk_res_plat FOREIGN KEY (id_res)
            REFERENCES reservation(id_res)
        )
    """);

        st.executeUpdate("""
        CREATE SEQUENCE seq_restaurant START WITH 200 INCREMENT BY 1
    """);

        st.close();
    }

    private void insererRestaurants(Connection connection, JSONArray restaurants) throws SQLException {

        String sql = "INSERT INTO restaurant (id_restaurant, nom_restaurant, adresse_restaurant, coord_gps) VALUES (?, ?, ?, ?) ";

        PreparedStatement ps = connection.prepareStatement(sql);

        int id = 1;

        for (int i = 0; i < restaurants.length(); i++) {

            JSONObject r = restaurants.getJSONObject(i);

            // Récupération des données de l'arrayJson
            String nom = r.optString("nom", "Sans nom");
            double lon = r.optDouble("longitude");
            double lat = r.optDouble("latitude");
            String coord = lat + "," + lon;

            String numero = r.optString("contact:housenumber", "");
            String rue = r.optString("contact:street", "");

            String adresse;

            if (numero == "" && rue == "") {
                adresse = "Adresse inconnue";
            } else {
                adresse = numero + " " + rue;
            }

            ps.setInt(1, id++);
            ps.setString(2, nom);
            ps.setString(3, adresse);
            ps.setString(4, coord);

            // Pour executer toutes les requetes jdbc en meme temps
            ps.addBatch();
        }

        ps.executeBatch();
        ps.close();
    }
}
