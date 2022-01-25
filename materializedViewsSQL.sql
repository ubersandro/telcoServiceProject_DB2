/**
  VIEW TABLES corresponding to the materialized views used for sales
  report statistics.
 */

-- number of total purchases per package per VP
CREATE VIEW totalSalesSPVP AS
SELECT O.packageID AS 'PACKAGE', O.vpMonths AS 'ValidityPeriod', COUNT(*) AS 'Sales'
FROM `Order` O
GROUP BY O.packageID, O.vpMonths;

-- number of total purchases per package
CREATE VIEW totalSalesSP AS
SELECT O.packageID AS 'PACKAGE', COUNT(*) AS 'Sales'
FROM `Order` O
GROUP BY O.packageID;

-- number of total purchases per package with optional products
CREATE VIEW totalPurchasesSPwithOptionalProducts(package, purchasesOPT) AS
SELECT O.packageID AS package, COUNT(DISTINCT(O.id)) AS purchasesOPT
FROM `Order` O INNER JOIN
     `Includes` I
GROUP BY O.packageID;

-- number of total purchases per package WITHOUT optional products
CREATE VIEW totalPurchasesSPwithoutOptionalProducts (package, purchases) AS
    SELECT T.PACKAGE AS package, (T.Sales - W.purchasesOPT) AS purchases
    FROM totalSalesSP T, totalPurchasesSPwithOptionalProducts W
    WHERE T.PACKAGE = W.package;

-- INSOLVENT USERS
CREATE VIEW insolventUsers AS
SELECT *
FROM Consumer C
WHERE C.status = 1;

-- SUSPENDED ORDERS
CREATE VIEW suspendedOrders AS
SELECT *
FROM `Order` O
WHERE O.status = 1;

-- ALERTS
CREATE VIEW alerts AS
SELECT *
FROM  Auditing;

-- average optional products
/**
  REPHRASED : for each service package
  the average number of optional products inserted in an order
  (sum of the orders of the SP/total number of products ever sold with the sp)
 */
CREATE VIEW avgOptsSoldSP(package, avgOpts) AS
    SELECT O.packageID AS package, COUNT(O.id)/COUNT (I.productName) AS avgOpts
FROM `Order` O RIGHT JOIN  `Includes` I on O.id = I.orderId
GROUP BY O.packageID, O.id;
/**
  @TODO check last query  
 */
