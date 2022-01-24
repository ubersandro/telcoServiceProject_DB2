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
    counter  INT         NOT NULL DEFAULT '0',
    PRIMARY KEY (`username`),
    CONSTRAINT fKeyCons FOREIGN KEY (username) REFERENCES TelcoUser (username) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT nonNegCounter CHECK (counter >= 0)
    -- we assume that Insolvent == 0 IFF FALSE and Insolvent > 0 IFF TRUE
);

CREATE TABLE Auditing
( -- @TODO sign constraints
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

-- SALES REPORT INFO

/**
  The table contains the number of times a ServicePackage has been purchased with a given validity period.
 */
CREATE TABLE purchasesPerPackageVP
( -- MATERIALIZED VIEW TABLE
    servicePackage       INT,
    validityPeriodMonths INT,
    CONSTRAINT PRIMARY KEY (servicePackage, validityPeriodMonths),
    counter              INT DEFAULT '0',
    CONSTRAINT FOREIGN KEY fk123 (servicePackage, validityPeriodMonths) REFERENCES HasValidity (packageID, validityMonths) ON DELETE CASCADE ON UPDATE CASCADE
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
    sales       INT,
    CONSTRAINT FOREIGN KEY (productName) REFERENCES OptionalProduct (name) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT PRIMARY KEY (productName)
);


-- TUPLE INSERTION
/*LOCK TABLES Consumer WRITE , Service WRITE , MobilePhoneService WRITE , FixedInternetService WRITE ,
    FixedPhoneService WRITE, SPS WRITE, OptionalProduct WRITE, Offers WRITE, HasValidity WRITE, TelcoUser write , Employee write,
    ServicePackage WRITE,ValidityPeriod WRITE;
*/
INSERT INTO TelcoUser (username, email, password, DTYPE)
VALUES ('consumerA', 'A@A.it', 'A', 'CONS'),
       ('employeeB', 'B@B.it', 'B', 'EMP');
INSERT INTO Consumer (username)
VALUES ('consumerA');
INSERT INTO Employee (username)
VALUES ('employeeB');

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
       ('opt2', 3.4);

INSERT INTO Offers (productName, packageID)
VALUES ('opt1', 1),
       ('opt2', 1);
INSERT INTO Offers (productName, packageID)
VALUES ('opt1', 3),
       ('opt2', 3);

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
VALUES (1, 12, 1.0),
       (1, 24, 2.0),
       (1, 36, 3.0);
INSERT INTO HasValidity (packageID, validityMonths, monthlyFee)
VALUES (2, 12, 11.0),
       (2, 24, 21.0),
       (2, 36, 32.0);
INSERT INTO HasValidity (packageID, validityMonths, monthlyFee)
VALUES (3, 12, 12.0),
       (3, 24, 22.0),
       (3, 36, 33.0);

INSERT INTO `Order` (DATE, TIME, TOTALVALUE, STARTINGDATE, CONSUSERNAME, PACKAGEID, VPMONTHS)
VALUES ('2022-01-21', '123456', '200.0', '2022-01-22', 'consumerA', '1', '12');

-- only three values

 /*UNLOCK TABLES;*/

-- PROCEDURE DEFINITION
CREATE OR REPLACE PROCEDURE updateProductSales(IN orderID INT)
BEGIN
    DECLARE currentProduct VARCHAR(45);
    DECLARE done INT DEFAULT 0;


    DECLARE curs CURSOR FOR
        SELECT I.productName AS optionalProduct FROM Includes I WHERE I.orderId = orderId;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    OPEN curs;

    salesUpdateLoop : LOOP
        FETCH curs INTO currentProduct;
        IF done THEN
            LEAVE salesUpdateLoop;
        END IF;
        -- UPDATE SALES REPORT TABLE

        IF ((SELECT count(*) FROM optionalProduct_sales S WHERE S.productName=currentProduct) != '0') THEN
            UPDATE optionalProduct_sales X SET sales=sales+1 WHERE X.productName=currentProduct;
        ELSE -- THE TUPLE DOES NOT EXIST
            INSERT INTO optionalProduct_sales(productName, sales) VALUES (currentProduct, '1');
        END IF;


    END LOOP;
    CLOSE curs ;
END;


-- TRIGGERS DEFINITION

CREATE OR REPLACE TRIGGER createServiceActivationSchedule
    AFTER UPDATE
    ON `Order`
    FOR EACH ROW
BEGIN
    IF new.status = '2' THEN -- WHENEVER AN ORDER IS MARKED AS PAID (STATUS = 2)
    -- serviceActivationSchedule creation
        INSERT INTO ServiceActivationSchedule(orderID, endDate)
        VALUES (new.id, ADDDATE(new.startingDate, INTERVAL NEW.vpMonths MONTH));
        -- end serviceActivationSchedule creation

        -- purchasesPerPackageVP BEGIN
        IF ((SELECT COUNT(*) FROM purchasesPerPackageVP P WHERE P.servicePackage = NEW.packageID) = '0')
        THEN
            INSERT INTO purchasesPerPackageVP(servicePackage, validityPeriodMonths, counter)
            VALUES (NEW.packageID, NEW.vpMonths, 1);
        ELSE
            UPDATE purchasesPerPackageVP
            SET counter=counter + 1
            WHERE servicePackage = NEW.packageID
              AND validityPeriodMonths = NEW.vpMonths;
        END IF;
        -- purchasesPerPackageVP END

        -- purchaseSP_sumOPTS_purWithOPTS BEGIN
        IF ((SELECT COUNT(*) FROM Includes I WHERE I.orderId = NEW.id) > '0') THEN -- THE PURCHASE INCLUDES OPTIONAL PRODUCTS
            IF ((SELECT COUNT(*) FROM salesSP_OP P WHERE P.packageID = NEW.packageID) > '0') -- THE TUPLE EXISTS
            THEN
                UPDATE salesSP_OP
                SET purchasesWithOptionalProducts = purchasesWithOptionalProducts + '1' ,
                    totalOptionalProducts =   totalOptionalProducts + (SELECT COUNT(*) FROM Includes I WHERE I.orderId = NEW.id)
                WHERE packageID = NEW.packageID;
            ELSE -- THE TUPLE DOES NOT EXIST, CREATE IT
                INSERT INTO salesSP_OP(packageID, totalOptionalProducts, purchasesWithOptionalProducts)
                VALUES (NEW.packageID, (SELECT COUNT(*) FROM Includes I WHERE I.orderId = NEW.id), 1);
            END IF;
            -- product sales statistics update
            CALL updateProductSales(new.id);
        END IF; -- purchase with optional products

        -- end purchases update
    END IF;
END;


--  QUERIES

--  TOTAL PURCHASES PER PACKAGE

SELECT X.servicePackage AS PACKAGE, sum(counter) AS SALES
FROM purchasesPerPackageVP X
GROUP BY X.servicePackage;

-- TOTAL PURCHASES PER PACKAGE AND VP

SELECT *
FROM purchasesPerPackageVP X;

-- TOTAL NUMBER OF SERVICE PACKAGE SALES WITH AT LEAST ONE OPTIONAL PRODUCT
SELECT X.packageID AS ServicePackage, X.purchasesWithOptionalProducts AS "Purchases with at least one product"
FROM salesSP_OP X;

-- TOTAL NUMBER OF SERVICE PACKAGE SALES WITHOUT ANY OPTIONAL PRODUCTS

SELECT X.servicePackage AS "ServicePackage ID",
       sum(counter) -
       (SELECT COUNT(Y.purchasesWithOptionalProducts)
        FROM salesSP_OP Y
        WHERE Y.packageID = X.servicePackage)
                        AS "total purchases with no optional products included"
FROM purchasesPerPackageVP X
GROUP BY X.servicePackage;
-- NOT NECESSARY

-- AVERAGE NUMBER OF  OPTIONAL PRODUCT SOLD TOGETHER WITH EACH SERVICE PACKAGE
SELECT X.packageID                                                       as ServicePackage,
       sum(X.totalOptionalProducts) / sum(purchasesWithOptionalProducts) as "Average number of optional products included"
FROM salesSP_OP X
GROUP BY X.packageID;

-- BEST SELLER OPTIONAL PRODUCT -> PRODUCT SOLD THE MAXIMUM NUMBER OF TIMES -> NON UNIQUE !
SELECT *
FROM optionalProduct_sales S
WHERE S.sales = (SELECT DISTINCT MAX(ops.sales) FROM optionalProduct_sales ops);
