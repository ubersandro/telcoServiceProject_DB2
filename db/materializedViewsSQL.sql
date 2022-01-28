/**
  VIEW TABLES corresponding to the materialized views used for sales
  report statistics.
 */

/* number of total purchases per package per VP

   This view consists of a query which counts the number of PAID orders grouping by their associated
   service package and validity period.*/
CREATE OR REPLACE VIEW totalSalesSPVP (PACKAGE, VALIDITYPERIOD, SALES) AS
SELECT O.packageID AS PACKAGE, O.vpMonths AS VALIDITYPERIOD, COUNT(*) AS SALES
FROM `Order` O
WHERE O.status = 2
GROUP BY O.packageID, O.vpMonths;

-- number of total purchases per package
/*
   This view consists of a query which counts the number of PAID orders grouping by their associated
   service package and validity period.*/
CREATE OR REPLACE VIEW totalSalesSP(PACKAGE, SALES) AS
SELECT S.PACKAGE AS PACKAGE, COUNT(*) AS SALES
FROM totalSalesSPVP S
GROUP BY S.PACKAGE;

/* number of total purchases per package with optional products
    This view is made up of a query which joins Order and Includes tables to compute how many orders include at least one optional product.
   The number of purchases of a given ServicePackage including at least one Optional Product is calculated grouping by the Service Package
   related to the Order tuples taken into consideration.
   The examined orders have to have been PAID.
   */
CREATE OR REPLACE VIEW totalPurchasesSPwithOptionalProducts(PACKAGE, PURCHASES) AS
SELECT O.packageID AS PACKAGE, COUNT(DISTINCT (O.id)) AS PURCHASES
FROM `Order` O
         INNER JOIN
     `Includes` I
     ON O.id = I.orderId
WHERE O.status = 2 -- PAID ORDERS ONLY
GROUP BY O.packageID;


-- number of total purchases per package WITHOUT optional products
/**
    The value is computed starting from the total sales of a given Service Package and subtracting to this quantity the sales that include
  at least one optional product. Preexistent views can be used for convenience of notation.
 */
CREATE OR REPLACE VIEW totalPurchasesSPwithoutOptionalProducts (PACKAGE, PURCHASES) AS
SELECT T.PACKAGE AS PACKAGE, (T.Sales - W.PURCHASES) AS PURCHASES
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

-- average optional products with each service package
/**
  The query computes the average number of optional products ever sold with a given service package for every service package.
  To do so it just divides total number of optional products ever sold by the number of purchases of the given SP (with or without Optional Products).
  NB: Division by zero does not occur because, taking any (PAID) order it is always related to a ServicePackage, whose sales cannot be 0 according
  to the way the content of the totalSalesSPVP view is computed.
 */
CREATE OR REPLACE VIEW avgOptsSoldSP(PACKAGE, AVGOPTS) AS
SELECT O.packageID AS PACKAGE,
       COUNT(*) / (SELECT COUNT(*)
                   FROM totalSalesSPVP X
                   WHERE X.PACKAGE = O.packageID)
                   AS AVGOPTS
FROM `Order` O,
     `Includes` I
WHERE O.id = I.orderId
  AND O.status = 2
GROUP BY O.packageID;


-- best seller optional product
/**
  This view computes a top-1 query. The strategy used to perform such a task
  consists in computing total sales of a given optional product joining Order table
  and Includes table to retrieve included optional products from (paid) orders and then sum the
  matching tuples grouping by the key of the Optional Product table to obtain total sales for each product.
  This being said, results are ordered according to the former sum values and result set is limited to 1 to obtain the best seller.
  In case more products are sold the same exact number of times and the given number equals the maximum of the sales,
  only one product is retrieved (presumably non-deterministically).
 */
CREATE OR REPLACE VIEW bestSellerOpt(PRODUCT, SALES) AS
SELECT I.productName PRODUCT, COUNT(*) AS SALES
FROM `Order` O,
     Includes I
WHERE O.status = 2
  AND O.id = I.orderId
GROUP BY I.productName
ORDER BY Sales
LIMIT 1;