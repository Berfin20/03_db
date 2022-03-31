create table if not exists sports_club (
    idClub varchar(50) not null primary key,
    clubName varchar(45) not null,
    streetName varchar(45) not null,
    postalCode int not null,
    city varchar(45) not null,
    email varchar(70) not null,
    phoneNumber int not null,
    constraint clubName unique (clubName),
    constraint email unique (email),
    constraint idClub unique (idClub),
    constraint phoneNumber unique (phoneNumber)
);;

create table if not exists age_group (
    id int not null,
    gender char(1) not null,
    ageFrom int not null,
    ageTo int not null,
    primary key (id)
);;

create table if not exists member (
    firstName varchar(45) not null,
    lastName varchar(45) not null,
    gender char(1) not null,
    birthday int not null,
    email varchar(70) not null,
    phoneNumber int not null,
    isContestant bit not null,
    primary key (firstName, lastName, gender, birthday, email),
    constraint email unique (email)
);;

create table if not exists event_type (
    name varchar(45) not null,
    age_group int not null,
    primary key (name, age_group),
    foreign key (age_group) REFERENCES age_group(id)
);;

create table if not exists event (
    eventType varchar(45) not null,
    date date not null,
    primary key (eventType, date),
    constraint date unique (date),
    foreign key (eventType) REFERENCES event_type (name)
);;

create table if not exists result (
    memberEmail varchar(70) not null,
    age_group int not null,
    startingNumber int not null,
    result int,
    primary key (memberEmail, age_group, startingNumber, result),
    constraint startingNumber unique (startingNumber),
    foreign key (memberEmail) REFERENCES member (email),
    foreign key (age_group) REFERENCES age_group (id)
);;

create table if not exists registration (
    email varchar(70) not null,
    firstName varchar(45) not null,
    lastName varchar(45) not null,
    gender char(1) not null,
    birthday int not null,
#     ageGroup int not null,
    clubName varchar(50) not null,
    eventType varchar(45) not null,
    date DATE not null,
#     startingNumber int not null,
#     result int
primary key (email, clubName, eventType, date),
foreign key (email, firstName, lastName, gender, birthday) REFERENCES member (email, firstName, lastName, gender, birthday),
foreign key (clubName) REFERENCES sports_club (clubName),
foreign key (eventType, date) REFERENCES event (eventType, date)
);;



