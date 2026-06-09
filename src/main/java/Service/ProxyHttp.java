package Service;
 
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
 
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
 
/**
 * Proxy HTTP entre le pont entre le navigateur et les services RMI.
 * Avec deux routes : 
 * {GET /restaurants} — appelle le service RMI "reservation" et retourne la liste JSON des restaurants.
 * {GET /incidents}   — appelle le service RMI "waze" et retourne les incidents de circulation en JSON.
 */
public class ProxyHttp {
 
    /**
     * Point d'entrée du proxy
     */
    public static void main(String[] args) {
 
        // Lecture des paramètres en ligne de commande
        if (args.length < 2) {
            System.err.println("Usage : java Service.ProxyHttp <hote_rmi> <port_http>");
            System.err.println("Exemple : java Service.ProxyHttp 192.168.1.42 8080");
            System.exit(1);
        }
 
        String rmiHost  = args[0];
        int httpPort = Integer.parseInt(args[1]);
 
        try {
            // Démarrage du serveur HTTP
            HttpServer server = HttpServer.create(new InetSocketAddress(httpPort), 0);
 
            // Route pour restaurants
            server.createContext("/restaurants", (HttpExchange exchange) -> {
                String json;
                try {
                    Registry reg = LocateRegistry.getRegistry(rmiHost, 1099);
                    ServiceInterface svc = (ServiceInterface) reg.lookup("reservation");
                    json = svc.recupererDonnees();
                } catch (Exception e) {
                    json = "{\"status\":false,\"message\":\"Erreur RMI reservation : " + e.getMessage() + "\"}";
                    System.err.println("Erreur /restaurants : " + e.getMessage());
                }
                sendJson(exchange, json);
            });
 
            // Route pour incidents
            server.createContext("/incidents", (HttpExchange exchange) -> {
                String json;
                try {
                    Registry reg = LocateRegistry.getRegistry(rmiHost, 1099);
                    ServiceWazeInterface waze = (ServiceWazeInterface) reg.lookup("waze");
                    json = waze.recupererDonnees();
                } catch (Exception e) {
                    json = "{\"status\":false,\"message\":\"Erreur RMI waze : " + e.getMessage() + "\"}";
                    System.err.println("Erreur /incidents : " + e.getMessage());
                }
                sendJson(exchange, json);
            });
 
            // Démarre avec un thread pool par défaut
            server.setExecutor(null);
            server.start();
 
            System.out.println("Proxy HTTP démarré sur le port " + httpPort);
            System.out.println("  -> Restaurants  (RMI « reservation » sur " + rmiHost + ":1099)");
            System.out.println("  -> Incidents    (RMI « waze »        sur " + rmiHost + ":1099)");
 
            // Bloque le thread principal
            Thread.currentThread().join();
 
        } catch (IOException e) {
            System.err.println("Impossible de démarrer le serveur HTTP sur le port " + httpPort + " : " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
 
    /**
     * Envoie une réponse JSON avec les en-têtes CORS
     */
    private static void sendJson(HttpExchange exchange, String json) {
        try {
            // En-têtes CORS
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin",  "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
 
            byte[] bytes = json.getBytes("UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
 
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi de la réponse HTTP : " + e.getMessage());
        }
    }
}
