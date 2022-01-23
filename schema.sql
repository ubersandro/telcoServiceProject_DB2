DROP DATABASE IF EXISTS telcoServiceDB;
CREATE DATABASE telcoServiceDB;
USE telcoServiceDB;

CREATE TABLE TelcoUser
(
    username VARCHAR(45) NOT NULL,
    email    VARCHAR(45) NOT NULL,
    password VARCHAR(45) NOT NULL,
    CONSTRAINT pkeyUser PRIMARY KEY (username),
    DTYPE varchar(45)-- included to map inheritance
);

CREATE TABLE Employee
(
	username VARCHAR(45) NOT NULL, 
    	CONSTRAINT fKeyEmp FOREIGN KEY (username) REFERENCES TelcoUser (username) ON DELETE CASCADE ON UPDATE CASCADE,
	PRIMARY KEY (username)
);

CREATE TABLE Consumer
(
    username  VARCHAR(45) NOT NULL,
    status TINYINT     NOT NULL DEFAULT '0',
    counter   INT         NOT NULL DEFAULT '0',
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
    id INT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),
    DTYPE varchar(45)-- included to map inheritance
);


CREATE TABLE MobileInternetService
(
    id        INT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id),
    gigabytes INT NOT NULL,
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
    status       TINYINT(1) DEFAULT NULL,         -- NULL @creation, TRUE (FALSE) AFTER APPROVAL (REJECTION)
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

-- ServicePackage -<>- optionalProduct   @TODO VEDI NOME
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

-- SALES REPORT PART OF THE SCHEMA 
-- 1) purchasesPerPackage TABLE 
-- 2) purchasesPerPackageVP TABLE -> 1) diventa non necessaria 
-- 3) purchasesPerPackageW_WO_OptionalProducts
-- 4) packageAvgOptProdsSoldTogether
-- 5) insolvent users, suspended orders and alerts -> EJB, no table 
-- 6) optionalProducts sales to choose the best selling one  

/**
    QUERIES
  TOTAL PURCHASES PER PACKAGE

    SELECT sum(counter)
    FROM purchasesPerPackageVP X
    GROUP BY X.servicePackage

  TOTAL PURCHASES PER PACKAGE AND VP

    SELECT *
    FROM purchasesPerPackageVP X

 */
CREATE TABLE purchasesPerPackageVP( -- MATERIALIZED VIEW TABLE
    servicePackage     INT ,
    validityPeriodMonths INT,
    CONSTRAINT PRIMARY KEY (servicePackage, validityPeriodMonths),
    counter INT DEFAULT '0',
    CONSTRAINT FOREIGN KEY (servicePackage) REFERENCES HasValidity (packageID, validityMonths)
);
/**
  TRIGGERS
  T1) ON ORDER ACCEPTANCE (order marked as accepted, no matter what the previous state was) -> ADD SP WITH VP HERE

 */

/**
  The table contains the number of optional products ever included in a given package purchase together with the
  number of purchases of the package where at least an optional product was included.
 */
CREATE TABLE purchaseSP_sumOPTS_purWithOPTS (
    packageID INT,
    totalOptionalProducts INT, -- total number of optional products ever sold together with the package
    purchasesWithOptionalProducts INT, -- total number of purchases of the ServicePackage with AT LEAST ONE optional prod.
    CONSTRAINT FOREIGN KEY (packageID) REFERENCES ServicePackage(id),
    CONSTRAINT PRIMARY KEY (packageID)
);
-- QUERIES
    -- a) total value of sales WITH optional products
        SELECT X.packageID AS ServicePackage,  X.purchasesWithOptionalProducts AS "Purchases with at least one product"
        FROM  purchaseSP_sumOPTS_purWithOPTS X;
    -- b) total value of sales WITHOUT optional products -> TOTAL SALES - a)

    -- c) AVERAGE optional products sold with each servicePackage
        SELECT  X.packageID as ServicePackage, sum(X.totalOptionalProducts) / sum (purchasesWithOptionalProducts) as average
        FROM purchaseSP_sumOPTS_purWithOPTS X
        GROUP BY X.packageID;
/**
  TRIGGERS
  T1) ON ORDER ACCEPTANCE -> IF "THERE IS AT LEAST ONE OPT IN THE ORDER" THEN purchasesWithOptionalProducts+=1,  totalOptionalProducts+=optionalProducts;
  @TODO fire the trigger for each statement
  ALTERNATIVE
    FOR EACH TUPLE Order, Optional product included in Table Includes then do the modification to the table purchaseSP_sumOPTS_purWithOPTS
 */





CREATE TABLE optionalProduct_sales (
    productName VARCHAR (45),
    sales INT,
    CONSTRAINT FOREIGN KEY (productName) REFERENCES OptionalProduct(name),
    CONSTRAINT PRIMARY KEY (productName)
);

/**
  TRIGGERS:

  T1) ON ORDER ACCEPTANCE -> IF "THERE IS AT LEAST ONE OPT IN THE ORDER" THEN FOR EACH PROD INCREMENT  optionalProduct_sales.sales
  NO STATEMENT LEVEL --> SEE TRIGGER ABOVE
 */

/**
    QUERY :

    SELECT X.productName "Best seller Product"
    FROM optionalProduct_sales AS X
    WHERE X.sales IN (SELECT max(sales) FROM optionalProduct_sales);
  */






-- TUPLE INSERTION 
LOCK TABLES Consumer WRITE , Service WRITE , MobilePhoneService WRITE , FixedInternetService WRITE ,
    FixedPhoneService WRITE, SPS WRITE, OptionalProduct WRITE, Offers WRITE, HasValidity WRITE, TelcoUser write , Employee write,
    ServicePackage WRITE,ValidityPeriod WRITE ;

INSERT INTO TelcoUser (username, email, password, DTYPE) VALUES ("consumerA", "A@A.it", "A", "CONS"), ("employeeB", "B@B.it", "B", "EMP");
INSERT INTO Consumer (username) VALUES ("consumerA") ;
INSERT INTO Employee (username) VALUES ("employeeB") ;

INSERT INTO Service(DTYPE) VALUES ("MPS"),("FIS"), ("MIS"), ("FPS"); 

INSERT INTO FixedPhoneService(id) VALUES (4);
INSERT INTO FixedInternetService(id, gigabytes, fee ) VALUES (2, 10, 1.0);

INSERT INTO OptionalProduct (name, fee) VALUES ("opt1", 1.2), ("opt2",3.4);

INSERT INTO Offers (productName, packageID )VALUES ("opt1", 1), ("opt2", 1) ;

INSERT INTO SPS (packageID, serviceID) VALUES (1,4), (1,2); 

INSERT INTO HasValidity (packageID, validityMonths, monthlyFee) VALUES  (1, 12, 1.0), (1, 24, 2.0), (1, 36, 3.0);

INSERT INTO ServicePackage (name) VALUES ("SP1"), ("SP2"), ("SP3");
-- only three values
INSERT INTO ValidityPeriod(months) VALUES (12),(24),(36);
UNLOCK TABLES;
