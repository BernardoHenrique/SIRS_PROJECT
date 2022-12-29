drop table users_login cascade;
drop table McDonalds cascade;
drop table BurgerKing cascade;
drop table Subway cascade;
drop table ItalianRepublic cascade;
drop table user_profile cascade;

create table users_login
   (nome  varchar(80)	not null    unique,
    passwrd varchar(80) not null unique);

create table McDonalds
    (id numeric(20,0) not null unique,
     date varchar(80) not null,
     hour varchar(80) not null,
     numberPersons numeric(20,0),
    constraint pk_McDonalds primary key(id));

create table BurgerKing
    (id numeric(20,0) not null unique,
     date varchar(80) not null,
     hour varchar(80) not null,
     numberPersons numeric(20,0),
    constraint pk_BurgerKing primary key(id))

create table Subway
    (id numeric(20,0) not null unique,
     date varchar(80) not null,
     hour varchar(80) not null,
     numberPersons numeric(20,0),
    constraint pk_Subway primary key(id))

create table ItalianRepublic
    (id numeric(20,0) not null unique,
     date varchar(80) not null,
     hour varchar(80) not null,
     numberPersons numeric(20,0),
    constraint pk_ItalianRepublic primary key(id))    

create table user_profile
    (nome varchar(80) not null unique,
     passwrd varchar(80) not null unique,
     cardPoints numeric(20,0),
     creditCardNumber numeric(20,0));


insert into users_login values ('Ricardo Chaves', 'securitymaster');
insert into users_login values ('Miguel Pardal', 'mastersecurity');

insert into user_profile values ('Ricardo Chaves', 'securitymaster', 20, 123456789);
insert into user_profile values ('Miguel Pardal', 'mastersecurity', 20, 987654321);

insert into McDonalds values ('1319', '13/01/2023', '19:00', 20);
insert into McDonalds values ('1320', '13/01/2023', '20:00', 20);
insert into McDonalds values ('1321', '13/01/2023', '21:00', 20);
insert into McDonalds values ('1312', '13/01/2023', '12:00', 20);
insert into McDonalds values ('1313', '13/01/2023', '13:00', 20);
insert into McDonalds values ('1314', '13/01/2023', '14:00', 20);
insert into McDonalds values ('1419', '14/01/2023', '19:00', 20);
insert into McDonalds values ('1420', '14/01/2023', '20:00', 20);
insert into McDonalds values ('1421', '14/01/2023', '21:00', 20);
insert into McDonalds values ('1412', '14/01/2023', '12:00', 20);
insert into McDonalds values ('1413', '14/01/2023', '13:00', 20);
insert into McDonalds values ('1414', '14/01/2023', '14:00', 20);

insert into BurgerKing values ('1319', '13/01/2023', '19:00', 20);
insert into BurgerKing values ('1320', '13/01/2023', '20:00', 20);
insert into BurgerKing values ('1321', '13/01/2023', '21:00', 20);
insert into BurgerKing values ('1312', '13/01/2023', '12:00', 20);
insert into BurgerKing values ('1313', '13/01/2023', '13:00', 20);
insert into BurgerKing values ('1314', '13/01/2023', '14:00', 20);
insert into BurgerKing values ('1419', '14/01/2023', '19:00', 20);
insert into BurgerKing values ('1420', '14/01/2023', '20:00', 20);
insert into BurgerKing values ('1421', '14/01/2023', '21:00', 20);
insert into BurgerKing values ('1412', '14/01/2023', '12:00', 20);
insert into BurgerKing values ('1413', '14/01/2023', '13:00', 20);
insert into BurgerKing values ('1414', '14/01/2023', '14:00', 20);

insert into Subway values ('1319', '13/01/2023', '19:00', 20);
insert into Subway values ('1320', '13/01/2023', '20:00', 20);
insert into Subway values ('1321', '13/01/2023', '21:00', 20);
insert into Subway values ('1312', '13/01/2023', '12:00', 20);
insert into Subway values ('1313', '13/01/2023', '13:00', 20);
insert into Subway values ('1314', '13/01/2023', '14:00', 20);
insert into Subway values ('1419', '14/01/2023', '19:00', 20);
insert into Subway values ('1420', '14/01/2023', '20:00', 20);
insert into Subway values ('1421', '14/01/2023', '21:00', 20);
insert into Subway values ('1412', '14/01/2023', '12:00', 20);
insert into Subway values ('1413', '14/01/2023', '13:00', 20);
insert into Subway values ('1414', '14/01/2023', '14:00', 20);

insert into ItalianRepublic values ('1319', '13/01/2023', '19:00', 20);
insert into ItalianRepublic values ('1320', '13/01/2023', '20:00', 20);
insert into ItalianRepublic values ('1321', '13/01/2023', '21:00', 20);
insert into ItalianRepublic values ('1312', '13/01/2023', '12:00', 20);
insert into ItalianRepublic values ('1313', '13/01/2023', '13:00', 20);
insert into ItalianRepublic values ('1314', '13/01/2023', '14:00', 20);
insert into ItalianRepublic values ('1419', '14/01/2023', '19:00', 20);
insert into ItalianRepublic values ('1420', '14/01/2023', '20:00', 20);
insert into ItalianRepublic values ('1421', '14/01/2023', '21:00', 20);
insert into ItalianRepublic values ('1412', '14/01/2023', '12:00', 20);
insert into ItalianRepublic values ('1413', '14/01/2023', '13:00', 20);
insert into ItalianRepublic values ('1414', '14/01/2023', '14:00', 20);