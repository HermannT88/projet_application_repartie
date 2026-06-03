# SAE Projet Application Répartie - Juin 2026

## Lien GitHub : https://github.com/HermannT88/projet_application_repartie

## Lien Webetu : https://webetu.iutnc.univ-lorraine.fr/www/e97539u/

### Groupe : RA-IL 1

### Composition du groupe :

- CERDA DE ALMEIDA VILLACA Alexis
- FOUSSE Emelyne
- HERMANN Taïno
- SAGET Logan

## Compilation et Lancement (avec Maven)

Le projet utilise désormais Maven pour la gestion des dépendances et de la compilation automatique.
Il faut Maven installé !

```Powershell
winget install Apache.Maven
```

### Étape 1 : Compilation

Pour nettoyer et compiler le projet, exécutez la commande suivante à la racine du projet :

```bash
mvn clean compile
```

### Étape 2 : Lancer le Serveur (LancerService)

Pour éviter les conflits liés au VPN Cisco de l'IUT, nous forçons l'utilisation de l'adresse IP de rebouclage locale `127.0.0.1` à l'aide de l'option `-Djava.rmi.server.hostname=127.0.0.1` :

- **Sur Windows / Linux (PowerShell/Bash) :**
  ```bash
  mvn exec:java "-Dexec.mainClass=Service.LancerService" "-Dexec.args= Identifiant MotDePasse" "-Djava.rmi.server.hostname=127.0.0.1"
  ```

> **Explication des arguments :**
>
> - `-Dexec.mainClass="Service.LancerService"` : Indique la classe principale à exécuter (le serveur).
> - `-Dexec.args="<dbUsername> <dbPassword>"` : Arguments passés au programme.
> - `-Djava.rmi.server.hostname=127.0.0.1` : Force le serveur RMI à utiliser l'IP locale.

### Étape 3 : Lancer le Client

- **Sur Windows / Linux (PowerShell/Bash) :**
  ```bash
  mvn exec:java "-Dexec.mainClass=Client.Client" "-Dexec.args=127.0.0.1 1099"
  ```

> **Explication des arguments :**
>
> - `-Dexec.mainClass="Client.Client"` : Indique la classe principale à exécuter (le client).
> - `-Dexec.args="127.0.0.1 1099"` : Arguments passés au programme client.
>   - `127.0.0.1` : L'adresse IP du serveur hébergeant l'annuaire RMI (ici, le serveur local).
>   - `1099` : Le port de l'annuaire RMI (1099 par défaut).

### Informations complémentaires :

Il faut installer la dépendance Leafet :
npm install --save-dev @types/leaflet

1. Compiler le Java (sans Maven)
   Tu as besoin des JARs de dépendances (org.json et ojdbc11). Maven les a téléchargés dans ton répertoire .m2. La compilation manuelle :

$json_jar = "$env:USERPROFILE\.m2\repository\org\json\json\20231013\json-20231013.jar"
$ojdbc_jar = "$env:USERPROFILE\.m2\repository\com\oracle\database\jdbc\ojdbc11\23.2.0.0\ojdbc11-23.2.0.0.jar"
javac -cp "$json_jar;$ojdbc*jar" -d target/classes src/main/java/Service/*.java src/main/java/Client/\_.java

2. Lancer les Services RMI (LancerService)
   $json_jar = "$env:USERPROFILE\.m2\repository\org\json\json\20231013\json-20231013.jar"
   $ojdbc_jar = "$env:USERPROFILE\.m2\repository\com\oracle\database\jdbc\ojdbc11\23.2.0.0\ojdbc11-23.2.0.0.jar"
   java -Djava.rmi.server.hostname=127.0.0.1 -cp "target/classes;$json_jar;$ojdbc_jar" Service.LancerService Identifiant MotDePasse
   Remplace Identifiant et MotDePasse par tes identifiants de base de données Oracle.

3. Lancer le Proxy HTTP
   Dans un second terminal :

$json_jar = "$env:USERPROFILE\.m2\repository\org\json\json\20231013\json-20231013.jar"
$ojdbc_jar = "$env:USERPROFILE\.m2\repository\com\oracle\database\jdbc\ojdbc11\23.2.0.0\ojdbc11-23.2.0.0.jar"
java -cp "target/classes;$json_jar;$ojdbc_jar" Service.ProxyHttp 127.0.0.1 8080

npx -y serve .
