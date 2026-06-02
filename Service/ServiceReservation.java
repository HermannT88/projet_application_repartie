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
    public Boolean reserverTable(String nom, String prenom, int nbClients, String telephonne, LocalDate date) throws RemoteException {
        String url = "jdbc:oracle:thin:@charlemagne.iutnc.univ-lorraine.fr:1521:infodb";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection connection = DriverManager.getConnection(url, args.length >= 2 ? args[0] : "", args.length >= 2 ? args[1] : "");
            connection.setAutoCommit(false);

            String findTableSql = "SELECT id_table FROM table_restau t WHERE t.capacite >= ? AND NOT EXISTS (SELECT 1 FROM reservation r WHERE r.id_table = t.id_table AND TRUNC(r.dates) = ?) AND ROWNUM = 1";
            try (PreparedStatement ps = connection.prepareStatement(findTableSql)) {
                ps.setInt(1, nbClients);
                ps.setDate(2, java.sql.Date.valueOf(date));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int idTable = rs.getInt("id_table");
                    rs.close();

                    String insertSql = "INSERT INTO reservation (id_res, id_table, nom_client, prenom_client, nb_convives, telephone, dates) VALUES (seq_restaurant.NEXTVAL, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement ins = connection.prepareStatement(insertSql)) {
                        ins.setInt(1, idTable);
                        ins.setString(2, nom);
                        ins.setString(3, prenom);
                        ins.setInt(4, nbClients);
                        ins.setString(5, telephonne);
                        ins.setDate(6, java.sql.Date.valueOf(date));

                        int updated = ins.executeUpdate();
                        connection.commit();
                        connection.close();
                        return updated == 1;
                    }
                } else {
                    rs.close();
                    connection.close();
                    return false; // no table available
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

