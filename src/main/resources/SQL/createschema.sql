use db_projekt;

SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS=0;

drop table if exists age_group;
drop table if exists person;
drop table if exists sports_club;
drop table if exists event;
drop table if exists contestant;
drop table if exists city;
drop table if exists result;

create table age_group(
                          gender char not null,
                          ageFrom int not null,
                          ageTo int not null,
                          primary key (gender, ageFrom, ageTo)
);

create table city(
                     postalCode int primary key,
                     city varchar(50)
);

create table person(
                       email varchar(70) not null primary key,
                       firstName varchar(45) not null,
                       lastName varchar(45) not null,
                       phoneNumber int,
                       houseNumber int,
                       streetName varchar(50),
                       postalCode int,
                       birthday date not null,
                       gender char not null,
                       isContestant bit not null,
                       foreign key (postalCode) references city (postalCode)
);

create table sports_club(
                            clubId varchar(7) not null primary key,
                            clubName varchar(50) not null,
                            email varchar(100),
                            phoneNumber int ,
                            houseNumber int,
                            streetName varchar(50) ,
                            postalCode int ,
                            foreign key (postalCode) references city (postalCode)
);

create table event(
                      eventType varchar(20),
                      date date,
                      club varchar(7) not null,
                      primary key (eventType,date,club),
                      foreign key (club) references sports_club (clubId)
);

create table contestant (
                            contestantEmail varchar(70) not null,
                            startingNumber int not null,
                            primary key (startingNumber),
                            foreign key (contestantEmail) references person (email)
);

create table result(
                       startingNumber int not null,
                       eventType varchar(20),
                       date date,
                       club varchar(7) not null,
                       time time,
                       primary key (startingNumber, eventType),
                       foreign key (eventType, date, club) references event (eventType, date, club),
                       foreign key (startingNumber) references contestant (startingNumber)
);

INSERT age_group
VALUES ('M','18','25'),
       ('K','18','25'),
       ('M','26','33'),
       ('K','26','33'),
       ('M','34','41'),
       ('K','34','41'),
       ('M','42','49'),
       ('K','42','49');

SELECT * FROM age_group;

INSERT person
VALUES ('beastgamer@gamermail.com','Frederik', 'Hansen', '78945612', 58,'Hovedgaarden','2605','2001-05-09','M',1),
       ('Maidenless@gmail.com','Signe', 'Mortensen', '54842194',121,'Frederikssundsvej','2700','1999-08-02','K',1),
       ('Headass846@gmail.com','Charlotte', 'Christiansen', '14828594',123,'Frederikssundsvej','2700','1998-02-28','K',1),
       ('Stonks@gmail.com','Jakob', 'Ditlovsen', '45718946',25,'Ådalen','2300','1997-01-01','M',0),
       ('Incredible@gmail.com','Mette', 'Jakobsen', '45671584',2,'Henriksvej','2600','1989-06-08','K',0),
       ('Bravesoul@gmail.com','Micheal', 'Paulsen', '54872648',42,'Ågården','2635','1990-9-18','M',1),
       ('MobileATT@gmail.com','Cille', 'Jensen', '15845654',26,'Strandgården','2635','1980-09-20','K',1);

SELECT * FROM person;

INSERT sports_club VALUES
                       ('frihjul','Fri Hjul',null,null,null,null,null),
                       ('sortsol','Sort Sol',null,null,null,null,null),
                       ('DraKri','DrageKrigerne','Dragekrigerne420@whatmail.com','21125895',24,'Oasen','2635'),
                       ('OnlBoy','OnlyBoys','OnlyBoys1969@notmail.com','69420178',23,'Charlotteparken','2600'),
                       ('FemPow','FemalePower','FemalePower1999@femmail.com','56874945',22,'Frederiksgade','2200'),
                       ('FrePri','FreshPrince','FreshPrince568@fremail.com','25896314',21,'Friske prinsgade','2200');
SELECT * FROM sports_club;

INSERT event
VALUES ('10km','2022-05-22','sortsol'),
       ('5km','2022-05-22','sortsol'),
       ('mtb','2022-05-04','frihjul');

SELECT * FROM event;

INSERT contestant
VALUES ('beastgamer@gamermail.com',444),
       ('Maidenless@gmail.com',5555),
       ('Maidenless@gmail.com',654),
       ('MobileATT@gmail.com',6454),
       ('Headass846@gmail.com',354);

SELECT * FROM contestant;

INSERT INTO result values
                       (444,'mtb','2022-05-22', 'frihjul', '00:20:10'),
                       (555,'mtb','2022-05-22', 'frihjul', '00:19:19'),
                       (654,'mtb','2022-05-22', 'frihjul', '00:02:02'),
                       (6454,'mtb','2022-05-22', 'frihjul', '00:18:10'),
                       (354,'mtb','2022-05-22', 'frihjul', '00:05:05');

SELECT * FROM result;

CREATE OR REPLACE VIEW best_times AS
SELECT startingNumber, time, eventType
FROM result
WHERE eventType = 'mtb'
ORDER BY time
LIMIT 3;

SELECT * FROM db_projekt.best_times;