drop table if exists users_login;
drop table if exists user_profile;
drop table bookings;

create table user_profile
    (nome varchar(80) not null unique,
     cardPoints numeric(20,0),
     3digit numeric(20,0),
     experationDate varchar(80),
     creditCardNumber numeric(20,0));

create table bookings
    (namePerson varchar(80) not null,
     NameRestaurant varchar(80) not null,
     nrPeople numeric(20,0),
     id varchar(80) not null
     creditCardNumber numeric(20,0));