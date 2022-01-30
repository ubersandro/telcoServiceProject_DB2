/**
  VIEW TABLES corresponding to the materialized views used for sales
  report statistics.
 */

CREATE OR REPLACE VIEW totalSalesSPVP (PACKAGE, VALIDITYPERIOD, SALES) AS
SELECT HV.packageID AS PACKAGE, HV.validityMonths AS VALIDITYPERIOD,
       (SELECT COUNT(O.id)
        FROM `Order` O
        WHERE O.status=2 AND O.vpMonths=HV.validityMonths AND O.packageID = HV.packageID)
        AS SALES
FROM HasValidity HV ;

-- number of total purchases per package
CREATE OR REPLACE VIEW totalSalesSP(PACKAGE, SALES) AS
SELECT S.PACKAGE AS PACKAGE, SUM(S.SALES) AS SALES
FROM totalSalesSPVP S
GROUP BY S.PACKAGE;

CREATE OR REPLACE VIEW totalPurchasesSPwithOptionalProducts(PACKAGE, PURCHASES) AS -- V2
SELECT SP.id AS PACKAGE,
       (SELECT count(DISTINCT(O.id))
       FROM `Order` O INNER JOIN Includes I ON O.id = I.orderId
       WHERE O.packageID = SP.id AND O.status = 2) AS SALESWOPT
FROM ServicePackage SP;



/**
    The query counts the number of order with no optional product associated grouping by the package ID of the ServicePackage associated with the orders.
 */
CREATE OR REPLACE VIEW totalPurchasesSPwithoutOptionalProducts (PACKAGE, PURCHASES) AS
SELECT T.PACKAGE PACKAGE, T.SALES - TOPT.PURCHASES
FROM totalSalesSP T, totalPurchasesSPwithOptionalProducts TOPT
WHERE T.PACKAGE = TOPT.PACKAGE;




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


CREATE OR REPLACE VIEW totalProductsPerOrderSP(ord, package, products) AS
SELECT O.id, O.packageID, COUNT(I.productName)
FROM `Order` O LEFT OUTER JOIN Includes I ON O.id = I.orderId
WHERE O.status = 2
GROUP BY O.id;

CREATE OR REPLACE VIEW avgOptsSoldSP(PACKAGE, AVGOPTS) AS
SELECT SP.id, avg(T.products)
FROM ServicePackage SP,totalProductsPerOrderSP T
WHERE SP.id = T.package
GROUP BY  SP.id;


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
ORDER BY Sales DESC
LIMIT 1;