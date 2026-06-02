package Service;

import java.rmi.RemoteException;
import java.sql.*;
import java.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;

public class ServiceReservation implements ServiceInterface {

    private String[] args;

    public ServiceReservation() {
        this.args = new String[0];
    }

    public ServiceReservation(String[] args) {
        this.args = args != null ? args.clone() : new String[0];
    }

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

    @Override
    public String reserverTable(int idResto,String nom, String prenom, int nbClients, String telephonne, LocalDate date) throws RemoteException {
        String url = "jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            try (Connection connection = DriverManager.getConnection(url, args.length >= 2 ? args[0] : "", args.length >= 2 ? args[1] : "")) {
                connection.setAutoCommit(false);

                String findTableSql = "SELECT t.id_table FROM table_restau t "
                        + "WHERE t.id_restaurant = ? AND t.capacite >= ? "
                        + "AND NOT EXISTS (SELECT 1 FROM reservation r WHERE r.id_table = t.id_table AND r.dates < ? AND r.dates_fin > ?) "
                        + "FOR UPDATE SKIP LOCKED";

                try (PreparedStatement ps = connection.prepareStatement(findTableSql)) {
                    ps.setInt(1, idResto);
                    ps.setInt(2, nbClients);
                    ps.setDate(3, Date.valueOf(date));
                    ps.setDate(4, Date.valueOf(date));

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int idTable = rs.getInt("id_table");
                            rs.close();

                            String insertSql = "INSERT INTO reservation (id_res, id_table, nom_client, prenom_client, nb_convives, telephone, dates, dates_fin, montant) "
                                    + "VALUES (seq_restaurant.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement ins = connection.prepareStatement(insertSql)) {
                                ins.setInt(1, idTable);
                                ins.setString(2, nom);
                                ins.setString(3, prenom);
                                ins.setInt(4, nbClients);
                                ins.setString(5, telephonne);
                                ins.setDate(6, Date.valueOf(date));
                                ins.setTimestamp(7, null);
                                ins.setInt(8, 0);

                                int updated = ins.executeUpdate();
                                connection.commit();
                                return updated == 1 ? "Réservation confirmée pour la table " + idTable : "Échec de la réservation.";
                            }
                        }
                    }

                    String countSql = "SELECT COUNT(*) AS candidate_count FROM table_restau t "
                            + "WHERE t.id_restaurant = ? AND t.capacite >= ? "
                            + "AND NOT EXISTS (SELECT 1 FROM reservation r WHERE r.id_table = t.id_table AND r.dates < ? AND r.dates_fin > ?)";
                    try (PreparedStatement countStmt = connection.prepareStatement(countSql)) {
                        countStmt.setInt(1, idResto);
                        countStmt.setInt(2, nbClients);
                        countStmt.setTimestamp(3, start);
                        countStmt.setTimestamp(4, end);
                        try (ResultSet countRs = countStmt.executeQuery()) {
                            if (countRs.next() && countRs.getInt("candidate_count") > 0) {
                                connection.commit();
                                return "Toutes les tables disponibles sont momentanément verrouillées. Veuillez patienter et réessayer.";
                            }
                        }
                    }
                    connection.commit();
                    return "Aucune table disponible pour ce restaurant à cette date.";
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return "Erreur de réservation : " + e.getMessage();
        }
    }
}

