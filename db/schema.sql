DROP DATABASE IF EXISTS telcoServiceDB;
CREATE DATABASE telcoServiceDB;
USE telcoServiceDB;

CREATE TABLE TelcoUser
(
    username VARCHAR(45) NOT NULL,
    email    VARCHAR(45) NOT NULL,
    password VARCHAR(45) NOT NULL,
    CONSTRAINT pkeyUser PRIMARY KEY (username),
    DTYPE    varchar(45)-- included to map inheritance
);

CREATE TABLE Employee
(
    username VARCHAR(45) NOT NULL,
    CONSTRAINT fKeyEmp FOREIGN KEY (username) REFERENCES TelcoUser (username) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (username)
);

CREATE TABLE Consumer
(
    username VARCHAR(45) NOT NULL,
    status   TINYINT     NOT NULL DEFAULT '0',
    PRIMARY KEY (`username`),
    CONSTRAINT fKeyCons FOREIGN KEY (username) REFERENCES TelcoUser (username) ON DELETE CASCADE ON UPDATE CASCADE
    -- we assume that Insolvent == 0 IFF FALSE and Insolvent > 0 IFF TRUE
);


CREATE TABLE Auditing
(
    username VARCHAR(45)   NOT NULL PRIMARY KEY,
    time     TIME(0)       NOT NULL,
    date     DATE          NOT NULL,
    email    VARCHAR(45)   NOT NULL,
    value    DECIMAL(8, 3) NOT NULL,
    CONSTRAINT refUser FOREIGN KEY (username) REFERENCES Consumer (username)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE ValidityPeriod
(
    months INT,
    PRIMARY KEY (months),
    CONSTRAINT allowedValues CHECK (months = 12 OR months = 24 OR months = 36)
);



CREATE TABLE OptionalProduct
(
    name VARCHAR(45),
    fee  DECIMAL(8, 2) NOT NULL,
    PRIMARY KEY (name),
    CONSTRAINT noNegFee CHECK (fee >= 0)
);

CREATE TABLE Service
(
    id    INT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),
    DTYPE varchar(45)-- included to map inheritance
);


CREATE TABLE MobileInternetService
(
    id        INT           NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),
    gigabytes INT           NOT NULL,
    fee       DECIMAL(8, 2) NOT NULL,
    CONSTRAINT signConstraintsMIS CHECK (gigabytes >= 0 AND fee >= 0.00),
    CONSTRAINT FOREIGN KEY (id) REFERENCES Service (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE MobilePhoneService
(
    id              INT NOT NULL AUTO_INCREMENT,
    minutes         INT NOT NULL,
    extraMinutesFee DECIMAL(8, 2),
    SMSs            INT NOT NULL,
    extraSMSsFee    DECIMAL(8, 2),
    CONSTRAINT signConstraintsMPS CHECK (minutes >= 0 AND extraMinutesFee >= 0.00 AND
                                         SMSs >= 0 AND extraSMSsFee >= 0.00),
    PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (id) REFERENCES Service (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE FixedInternetService
(
    id        INT NOT NULL AUTO_INCREMENT,
    gigabytes INT NOT NULL,
    fee       DECIMAL(8, 3),
    CONSTRAINT signConstraintsFPS CHECK (gigabytes >= 0 AND fee >= 0.00),
    PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (id) REFERENCES Service (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE FixedPhoneService
(
    id INT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (id) REFERENCES Service (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE ServicePackage
(
    id   INT         NOT NULL AUTO_INCREMENT,
    name VARCHAR(45) NOT NULL,
    PRIMARY KEY (id)
);


-- NOTA : order status shouldn't be null. It should belong to {0,1,2].
/** MEANING 0 : NEWLY_CREATED
            1 : REJECTED
            2 : ACCEPTED
*/
CREATE TABLE `Order`
(
    id           integer       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    date         DATE          NOT NULL,
    time         TIME(3)       NOT NULL,
    totalValue   DECIMAL(8, 3) NOT NULL,
    startingDate DATE          NOT NULL,
    status       TINYINT(1) DEFAULT '0',          -- NULL @creation, TRUE (FALSE) AFTER APPROVAL (REJECTION)
    consUsername VARCHAR(45)   NOT NULL,
    packageID    INT           NOT NULL,
    vpMonths     INT           NOT NULL,
    CONSTRAINT FOREIGN KEY (consUsername) REFERENCES Consumer (username),
    CONSTRAINT FOREIGN KEY (packageID) REFERENCES ServicePackage (id),
    CONSTRAINT FOREIGN KEY (vpMonths) REFERENCES ValidityPeriod (months),
    CONSTRAINT posValues CHECK (totalValue >= 0 ) -- OPTIONAL ?
    /*NO PROPAGATION of modifications --> an order isn't deleted even after the user who placed it is deleted @TODO check*/
);

-- SERVICE PACKAGE -<> - VALIDITYPERIOD
CREATE TABLE HasValidity
(
    packageID      INT,
    validityMonths INT,
    monthlyFee     DECIMAL(8, 3),
    PRIMARY KEY (packageID, validityMonths),
    CONSTRAINT FOREIGN KEY (packageID) REFERENCES ServicePackage (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FOREIGN KEY (validityMonths) REFERENCES ValidityPeriod (months) ON DELETE CASCADE ON UPDATE CASCADE
);
-- order -<>- optionalproduct
CREATE TABLE Includes
(
    orderId     INT         NOT NULL,
    productName VARCHAR(45) NOT NULL,
    CONSTRAINT PRIMARY KEY (orderId, productName),
    CONSTRAINT FOREIGN KEY (orderId) REFERENCES `Order` (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FOREIGN KEY (productName) REFERENCES OptionalProduct (name) ON DELETE CASCADE ON UPDATE CASCADE -- CAN AN OPTIONAL PRODUCT BE DELETED ?
);

-- ServicePackage -<>- optionalProduct
CREATE TABLE Offers
(
    productName VARCHAR(45) NOT NULL,
    packageID   INT         NOT NULL,
    CONSTRAINT PRIMARY KEY (productName, packageID),
    CONSTRAINT FOREIGN KEY (packageID) REFERENCES `ServicePackage` (id) ON DELETE CASCADE ON UPDATE CASCADE, -- PROPAGATION ???
    CONSTRAINT FOREIGN KEY (productName) REFERENCES OptionalProduct (name) ON DELETE CASCADE ON UPDATE CASCADE
);
-- ServicePackage -<>- Service
CREATE TABLE SPS
(
    packageID INT NOT NULL,
    serviceID INT NOT NULL,
    CONSTRAINT PRIMARY KEY (packageID, serviceID),
    CONSTRAINT FOREIGN KEY (packageID) REFERENCES `ServicePackage` (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FOREIGN KEY (serviceID) REFERENCES Service (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE ServiceActivationSchedule
(
    endDate DATE NOT NULL,
    orderID INT  NOT NULL,
    CONSTRAINT PRIMARY KEY (orderID),
    CONSTRAINT FOREIGN KEY (orderID) REFERENCES `Order` (ID) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE Payment
(
    paymentId       INT AUTO_INCREMENT PRIMARY KEY ,
    user VARCHAR(45) NOT NULL ,
    orderID  INT NOT NULL ,
    date DATE NOT NULL ,
    time TIME NOT NULL ,
    totalValue DECIMAL (8,3) NOT NULL ,
    status   INT NOT NULL DEFAULT '0',
    CONSTRAINT u FOREIGN KEY (user) REFERENCES Consumer (username) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT o FOREIGN KEY (orderID) REFERENCES `Order` (id) ON DELETE CASCADE ON UPDATE CASCADE

);
-- SALES REPORT INFO

/**
  The table contains the number of times a ServicePackage has been purchased with a given validity period.
 */
CREATE TABLE purchasesPerPackageVP
( -- MATERIALIZED VIEW TABLE
    servicePackage       INT,
    validityPeriodMonths INT,
    CONSTRAINT PRIMARY KEY (servicePackage, validityPeriodMonths),
    counter              INT DEFAULT '0'
    -- CONSTRAINT FOREIGN KEY fk123 (servicePackage, validityPeriodMonths) REFERENCES HasValidity (packageID, validityMonths) ON DELETE CASCADE ON UPDATE CASCADE
);

/**
  The table contains the number of optional products ever included in a given package purchase together with the
  number of purchases of the package where at least an optional product was included.
 */
CREATE TABLE salesSP_OP
(
    packageID                     INT,
    totalOptionalProducts         INT, -- total number of optional products ever sold together with the package (ASSUMPTION: A PRODUCT CAN BE INCLUDED AND COUNTED MORE THAN ONE TIME
    purchasesWithOptionalProducts INT, -- total number of purchases of the ServicePackage with AT LEAST ONE optional prod.
    CONSTRAINT FOREIGN KEY (packageID) REFERENCES ServicePackage (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT PRIMARY KEY (packageID)
);

CREATE TABLE optionalProduct_sales
(
    productName VARCHAR(45),
    sales       INT NOT NULL DEFAULT '0',
    CONSTRAINT FOREIGN KEY (productName) REFERENCES OptionalProduct (name) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT PRIMARY KEY (productName)
);


