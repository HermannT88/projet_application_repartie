# SAE Projet Application Répartie - Juin 2026

## Liens
- **GitHub :** https://github.com/HermannT88/projet_application_repartie
- **Webetu :** https://webetu.iutnc.univ-lorraine.fr/www/e97539u/

### Groupe : RA-IL 1
**Composition du groupe :**
- CERDA DE ALMEIDA VILLACA Alexis
- FOUSSE Emelyne
- HERMANN Taïno
- SAGET Logan

---

## Architecture du Projet
Le projet est composé de deux services RMI indépendants (`reservation` et `waze`), d'un Proxy HTTP faisant le pont avec l'interface web, et d'une application frontend. Les services peuvent être lancés sur la même machine ou sur des ordinateurs différents.

## Prérequis
- **Maven** (pour la gestion des dépendances et la compilation Java).
- **Node.js** et **npm** (pour le build et le lancement du front-end).

*(Sous Windows, vous pouvez installer Maven avec `winget install Apache.Maven`)*

---

## Compilation

Pour nettoyer et compiler le projet, exécutez la commande suivante à la racine :

```bash
mvn clean compile
```

---

## Lancement (Configuration Multi-PC)

Si vous souhaitez lancer les services sur des **machines différentes**, vous devez spécifier l'adresse IP de chaque machine avec `-Djava.rmi.server.hostname=<IP_MACHINE>`. 
*(Si vous lancez tout sur la même machine, utilisez simplement `127.0.0.1` à la place de l'IP).*

### 1. Lancer le Service Réservation (Sur PC 1)
Identifiez l'adresse IP du PC 1 (ex: `192.168.1.10`).

```bash
mvn exec:java "-Dexec.mainClass=Service.LancerServiceReservation" "-Dexec.args=VotreIdentifiant VotreMotDePasse" "-Djava.rmi.server.hostname=192.168.1.10"
```
*(Remplacez `192.168.1.10` par l'IP de la machine hébergeant ce service)*

### 2. Lancer le Service Waze (Sur PC 2)
Identifiez l'adresse IP du PC 2 (ex: `192.168.1.20`).

```bash
mvn exec:java "-Dexec.mainClass=Service.LancerServiceWaze" "-Djava.rmi.server.hostname=192.168.1.20"
```
*(Remplacez `192.168.1.20` par l'IP de la machine hébergeant ce service)*

### 3. Lancer le Proxy HTTP (Sur n'importe quel PC)
Le proxy HTTP a besoin de l'adresse IP des deux services pour fonctionner. L'usage est : `<ip_reservation> <ip_waze> <port_http>`.

```bash
mvn exec:java "-Dexec.mainClass=Service.ProxyHttp" "-Dexec.args=192.168.1.10 192.168.1.20 8080"
```

### 4. Lancer le Serveur Web (Front-end)
Construisez le frontend et servez-le :

```bash
npm install --save-dev @types/leaflet
npm run build
npx -y serve .
```
Puis, ouvrez votre navigateur web sur `http://localhost:3000` (le port affiché par `serve`). L'application web interrogera le Proxy HTTP tournant sur le port `8080`.

---

## Lancer le Client en ligne de commande (CLI)

Vous pouvez aussi utiliser le programme client CLI pour effectuer des réservations. Il faut lui passer l'IP de la machine où tourne le **Service Réservation**.

```bash
mvn exec:java "-Dexec.mainClass=Client.Client" "-Dexec.args=192.168.1.10 1099"
```

---

## Explication des paramètres RMI importants

Afin de permettre la communication RMI sur des réseaux séparés ou contourner les problèmes de VPN (ex: Cisco de l'IUT), l'utilisation du paramètre `java.rmi.server.hostname` est primordiale :
- `-Djava.rmi.server.hostname=<IP>` : Force l'objet RMI exporté à inscrire cette adresse IP dans l'annuaire RMI, permettant ainsi aux clients distants de savoir où se connecter physiquement.
