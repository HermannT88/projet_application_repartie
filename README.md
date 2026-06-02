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

SELECT t.id_table
FROM table_restau t
WHERE t.capacite >= 2
AND NOT EXISTS (
    SELECT 1
    FROM reservation r
    WHERE r.id_table = t.id_table
    AND r.dates < TO_DATE('10/09/2026 21:30', 'DD/MM/YYYY HH24:MI')
    AND r.dates_fin > TO_DATE('10/09/2026 19:30', 'DD/MM/YYYY HH24:MI')
);