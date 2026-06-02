-- Table Restaurant
-- DROP TABLE restaurant CASCADE CONSTRAINTS;
create table restaurant
(
id_restaurant number(4),
nom_restaurant varchar2(50),
adresse_restaurant varchar2(80),
coord_gps varchar2(50),
primary key(id_restaurant)
);

-- Tuples de restaurant
insert into restaurant values(1, 'L''Excelsior', '50 Rue Henri Poincaré, Nancy', '48.6898, 6.1751');
insert into restaurant values(2, 'Le Capu', '31 Rue Gambetta, Nancy', '48.6901, 6.1812');
insert into restaurant values(3, 'Vins et Tartines', '25 Bis Rue des Ponts, Nancy', '48.6885, 6.1822');


-- Table Table_restau
-- DROP TABLE table_restau CASCADE CONSTRAINTS;
create table table_restau
(
id_table number(4),
id_restaurant number(4),
capacite number(2),
primary key(id_table),
constraint fk_table_restau foreign key (id_restaurant) references restaurant(id_restaurant)
);

-- Tuples de table_restau
insert into table_restau values(10, 1, 2);
insert into table_restau values(11, 1, 4);
insert into table_restau values(12, 1, 6);
insert into table_restau values(13, 2, 2);
insert into table_restau values(14, 2, 4);
insert into table_restau values(15, 3, 2);
insert into table_restau values(16, 3, 4);


-- Table Reservation
-- DROP TABLE reservation CASCADE CONSTRAINTS;
create table reservation
(
id_res number(4),
id_table number(4),
nom_client varchar2(40),
prenom_client varchar2(40),
nb_convives number(2),
telephone varchar2(15),
dates date,
dates_fin date,
montant number(3),
primary key (id_res),
constraint fk_res_table foreign key (id_table) references table_restau(id_table)
);

-- Tuples de reservation
insert into reservation values(100, 11, 'Dupont', 'Jean', 3, '0601020304', to_date('10/09/2026 19:30', 'dd/mm/yyyy hh24:mi' ), to_date('10/09/2026 21:30', 'dd/mm/yyyy hh24:mi'),100);
insert into reservation values(101, 16, 'Martin', 'Alice', 4, '0708091011', to_date('10/09/2026 20:30', 'dd/mm/yyyy hh24:mi' ), to_date('10/09/2026 22:30', 'dd/mm/yyyy hh24:mi'),80);
insert into reservation values(102, 13, 'Bernard', 'Luc', 2, '0611223344', to_date('11/09/2026 11:30', 'dd/mm/yyyy hh24:mi' ), to_date('10/09/2026 12:30', 'dd/mm/yyyy hh24:mi'),34 );

-- Table Plat
-- DROP TABLE plat CASCADE CONSTRAINTS;
create table plat
(
id_plat number(4),
id_res number(4),
libelle_plat varchar2(40),
prix_unitaire number(2),
quantite_stockee number(4),
primary key (id_plat),
constraint fk_res_plat foreign key (id_res) references reservation(id_res)
);

insert into plat values(1, 100, 'Gigot d''agneau', 25, 2);
insert into plat values(2, 100, 'Quiche Lorraine', 12, 1);
insert into plat values(3, 101, 'Bouchée à la Reine', 18, 4);
insert into plat values(4, 101, 'Macarons de Nancy', 8, 4);
insert into plat values(5, 102, 'Pâté Lorrain', 14, 2);

-- Séquence pour le numéro de réservation
-- DROP SEQUENCE seq_restaurant;
CREATE SEQUENCE seq_restaurant START WITH 200 INCREMENT BY 1;