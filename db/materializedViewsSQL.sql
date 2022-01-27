/**
  VIEW TABLES corresponding to the materialized views used for sales
  report statistics.
 */

-- number of total purchases per package per VP
-- this query counts the number of PAID orders grouping by their associated service package and validity period.
CREATE OR REPLACE VIEW totalSalesSPVP AS
SELECT O.packageID AS 'PACKAGE', O.vpMonths AS 'ValidityPeriod', COUNT(*) AS 'Sales'
FROM `Order` O
WHERE O.status = 2
GROUP BY O.packageID, O.vpMonths ;

-- number of total purchases per package
CREATE OR REPLACE VIEW totalSalesSP AS
SELECT S.PACKAGE AS 'PACKAGE', COUNT(*) AS 'Sales'
FROM totalSalesSPVP S
GROUP BY S.PACKAGE;

-- number of total purchases per package with optional products
-- STRATEGY : query the Order table grouping by servicePackage to
-- count the number of PAID orders with at least one associated optional product.
CREATE OR REPLACE VIEW totalPurchasesSPwithOptionalProducts(package, purchasesOPT) AS
SELECT O.packageID AS package, COUNT(DISTINCT (O.id)) AS purchasesOPT
FROM `Order` O
         INNER JOIN
     `Includes` I
ON O.id = I.orderId
WHERE O.status = 2 -- PAID ORDERS ONLY
GROUP BY O.packageID;

-- number of total purchases per package WITHOUT optional products
CREATE OR REPLACE VIEW totalPurchasesSPwithoutOptionalProducts (package, purchases) AS
SELECT T.PACKAGE AS package, (T.Sales - W.purchasesOPT) AS purchases
FROM totalSalesSP T,
     totalPurchasesSPwithOptionalProducts W

WHERE T.PACKAGE = W.package;

-- INSOLVENT USERS
CREATE OR REPLACE VIEW insolventUsers AS
SELECT *
FROM Consumer C
WHERE C.status = 1;

-- SUSPENDED ORDERS
CREATE OR REPLACE VIEW suspendedOrders AS
SELECT *
FROM `Order` O
WHERE O.status = 1;

-- average optional products
/**
  REPHRASED : for each service package
  the average number of optional products ever sold
  (total number of optional products ever sold / total number of purcases of the given SP)
 */
CREATE OR REPLACE VIEW avgOptsSoldSP(package, avgOpts) AS
SELECT O.packageID AS package, COUNT(*) / (SELECT COUNT(*)
                                                FROM totalSalesSPVP X
                                                WHERE X.PACKAGE = O.packageID)
                                                AS avgOpts
FROM `Order` O, `Includes` I
WHERE O.id = I.orderId AND O.status=2
GROUP BY O.packageID;

