package DTO;

public class Restaurant {

    private int idRestaurant;
    private String nomRestaurant;
    private String adresseRestaurant;
    private String coordGps;

    public Restaurant(int idRestaurant, String nomRestaurant,String adresseRestaurant, String coordGps) {
        this.idRestaurant = idRestaurant;
        this.nomRestaurant = nomRestaurant;
        this.adresseRestaurant = adresseRestaurant;
        this.coordGps = coordGps;
    }

    public int getIdRestaurant() {
        return idRestaurant;
    }

    public String getNomRestaurant() {
        return nomRestaurant;
    }

    public String getAdresseRestaurant() {
        return adresseRestaurant;
    }

    public String getCoordGps() {
        return coordGps;
    }
}