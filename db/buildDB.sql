-- TUPLE INSERTION
LOCK TABLES Consumer WRITE , Service WRITE , MobilePhoneService WRITE , FixedInternetService WRITE ,
    FixedPhoneService WRITE, SPS WRITE, OptionalProduct WRITE, Offers WRITE, HasValidity WRITE,
    TelcoUser write , Employee write, ServicePackage WRITE,ValidityPeriod WRITE , ServiceActivationSchedule WRITE,
    `Order` WRITE;
INSERT INTO TelcoUser (username, email, password, DTYPE)
VALUES ('A', 'A@A.it', 'A', 'CONS'),
       ('B', 'A@A.it', 'B', 'CONS'),
       ('admin', 'B@B.it', 'admin', 'EMP');
INSERT INTO Consumer (username)
VALUES ('A'),
       ('B');
INSERT INTO Employee (username)
VALUES ('admin');

INSERT INTO Service(DTYPE)
VALUES ('MPS'),
       ('FIS'),
       ('MIS'),
       ('FPS');
INSERT INTO MobilePhoneService(id, SMSs, minutes, extraSMSsFee, extraMinutesFee)
VALUES (1, 10, 10, 100.2, 400.4);
INSERT INTO FixedInternetService(id, gigabytes, fee)
VALUES (2, 10, 1.0);
INSERT INTO MobileInternetService(id, gigabytes, fee)
VALUES (3, 10, 1.0);
INSERT INTO FixedPhoneService(id)
VALUES (4);

INSERT INTO ServicePackage (name)
VALUES ('SP1'),
       ('SP2'),
       ('SP3');

INSERT INTO OptionalProduct (name, fee)
VALUES ('opt1', 1.2),
       ('opt2', 3.4),
       ('opt3', 1.2),
       ('opt4', 3.4),
       ('opt5', 1.2),
       ('opt6', 3.4);

INSERT INTO Offers (productName, packageID)
VALUES ('opt1', 1),
       ('opt2', 1);
INSERT INTO Offers (productName, packageID)
VALUES ('opt1', 3),
       ('opt2', 3);
INSERT INTO Offers (productName, packageID)
VALUES ('opt4', 2),
       ('opt2', 2),
       ('opt1', 2),
       ('opt5', 2);

INSERT INTO SPS (packageID, serviceID)
VALUES (1, 4),
       (1, 2);
INSERT INTO SPS (packageID, serviceID)
VALUES (3, 4),
       (3, 2);
INSERT INTO SPS (packageID, serviceID)
VALUES (2, 4);

INSERT INTO ValidityPeriod(months)
VALUES (12),
       (24),
       (36);

INSERT INTO HasValidity (packageID, validityMonths, monthlyFee)
VALUES (1, 12, 3.0),
       (1, 24, 2.0),
       (1, 36, 1.0);
INSERT INTO HasValidity (packageID, validityMonths, monthlyFee)
VALUES (2, 12, 33.0),
       (2, 24, 22.0),
       (2, 36, 11.0);
INSERT INTO HasValidity (packageID, validityMonths, monthlyFee)
VALUES (3, 12, 33.0),
       (3, 24, 22.0),
       (3, 36, 11.0);

SET @orderID=1 ;
INSERT INTO `Order`(id, date, time, totalValue, startingDate, consUsername, packageID, vpMonths) VALUES
(@orderID, '2022-11-01', '12:00:00', '2000.31','2022-12-12','A','2', '24');

INSERT INTO Includes(orderId, productName) VALUES (@orderID, 'opt1'), (@orderID, 'opt2');
UPDATE `Order` SET status=2 WHERE id=@orderID;

UNLOCK TABLES;
