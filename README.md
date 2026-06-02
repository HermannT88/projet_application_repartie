# SAE Projet Application Répartie - Juin 2026

## Lien GitHub : https://github.com/HermannT88/projet_application_repartie

## Lien Webetu : https://webetu.iutnc.univ-lorraine.fr/www/e97539u/

### Groupe : RA-IL 1

### Composition du groupe : 

* CERDA DE ALMEIDA VILLACA Alexis
* FOUSSE Emelyne
* HERMANN Taïno
* SAGET Logan


javac -cp lib/json-20231013.jar:lib/ojdbc11.jar:. Service/*.java Client/Client.java

rmiregistry 1099 2>/dev/null & disown

java -cp .:lib/json-20231013.jar:lib/ojdbc11.jar Service.LancerService e97539u <dbPassword>

java -cp .:lib/json-20231013.jar Client.Client 100.64.80.202 1099