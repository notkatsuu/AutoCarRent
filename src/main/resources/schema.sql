DROP TABLE IF EXISTS Booking;
DROP TABLE IF EXISTS Car;
DROP TABLE IF EXISTS CarCategory;
DROP TABLE IF EXISTS Customer;

CREATE TABLE Customer(
    id varchar (255) NOT NULL PRIMARY KEY,
    name varchar (255) NOT NULL,
    surname varchar (255) NOT NULL,
    address varchar (255) NOT NULL,
    driverLicense varchar (9)NOT NULL UNIQUE,
    birthDate date NOT NULL,
    totalSpent double NOT NULL
);

CREATE TABLE CarCategory(
    name varchar (7) NOT NULL PRIMARY KEY,
    depositPrice int NOT NULL,
    dailyPrice int NOT NULL
);

INSERT INTO CarCategory VALUES ('ECONOMY',500,30);
INSERT INTO CarCategory VALUES ('LUXURY',1200,80);
INSERT INTO CarCategory VALUES ('FAMILY',700,50);
INSERT INTO CarCategory VALUES ('MINI',400,20);

CREATE TABLE Car(
    id varchar (255) NOT NULL PRIMARY KEY,
    brand varchar (255) NOT NULL,
    model varchar (255) NOT NULL,
    category varchar (7) NOT NULL,
    licensePlate varchar (8) NOT NULL UNIQUE,
    carCondition double NOT NULL,
    CONSTRAINT FK_CarCategory FOREIGN KEY (category) REFERENCES  CarCategory(name)
);

CREATE TABLE Booking(
    id varchar (255) NOT NULL PRIMARY KEY,
    datePickUp date NOT NULL,
    dateReturn date NOT NULL,
    bookedCar varchar (255) NOT NULL,
    booker varchar (255) NOT NULL,
    state varchar (255) NOT NULL,
    CONSTRAINT FK_Booking_Car FOREIGN KEY (bookedCar) REFERENCES Car(id) ON DELETE RESTRICT,
    CONSTRAINT FK_Booking_Customer FOREIGN KEY (booker) REFERENCES Customer(id) ON DELETE RESTRICT
);
