drop table if exists users_login;
drop table if exists user_profile;

create table users_login
    (nome varchar(80) not null,
     password varchar(80) not null unique);

create table user_profile
    (nome varchar(80) not null,
     cardPoints numeric(20,0) not null,
     3digit numeric(20,0) not null,
     experationDate varchar(80) not null,
     creditCardNumber numeric(20,0) not null unique);

insert into users_login values ('Ricardo Chaves', 'mastersecurity');
insert into users_login values ('Miguel Pardal', 'securitymaster');
insert into users_login values ('David Matos', 'sirsMVP')