/**
    title :
  VIEW TABLES corresponding to the materialized views used for sales
  report statistics.
 */

-- NUMBER OF SALES PER SERVICE PACKAGE AND VALIDITY PERIOD
/**
  This view is made up of a query that counts the number of paid orders in the DB
  associated to a given couple <ServicePackage, ValidityPeriod> for each such couple.
 */
CREATE OR REPLACE VIEW totalSalesSPVP (PACKAGE, VALIDITYPERIOD, SALES) AS
SELECT HV.packageID AS PACKAGE, HV.validityMonths AS VALIDITYPERIOD,
       (SELECT COUNT(O.id)
        FROM `Order` O
        WHERE O.status=2 AND O.vpMonths=HV.validityMonths AND O.packageID = HV.packageID)
        AS SALES
FROM HasValidity HV ;


-- TOTAL SALES PER SERVICE PACKAGE
/**
  This view consists of a query which sums the sales per validity period grouping by the associated ServicePackage for each ServicePackage.
 */
CREATE OR REPLACE VIEW totalSalesSP(PACKAGE, SALES) AS
SELECT S.PACKAGE AS PACKAGE, SUM(S.SALES) AS SALES
FROM totalSalesSPVP S
GROUP BY S.PACKAGE;

-- TOTAL PURCHASES OF EACH SERVICE PACKAGE WITH AT LEAST ONE OPTIONAL PRODUCT
/**
  This view is based on a query which, for each ServicePackage, computes the total number of PAID orders of that package
  which include with AT LEAST ONE Optional Product.
 */
CREATE OR REPLACE VIEW totalPurchasesSPwithOptionalProducts(PACKAGE, PURCHASES) AS -- V2
SELECT SP.id AS PACKAGE,
       (SELECT count(DISTINCT(O.id))
       FROM `Order` O INNER JOIN Includes I ON O.id = I.orderId
       WHERE O.packageID = SP.id AND O.status = 2) AS SALESWOPT
FROM ServicePackage SP;


-- TOTAL PURCHASES OF EACH SERVICE PACKAGE WITHOUT OPTIONAL PRODUCTS
/**
    Given the results computed by the views "totalPurchasesSPwithOptionalProducts" and totalSalesSP,
  this view returns the difference between total sales (with or without optional products) and sales with optional products.
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

-- AVERAGE NUMBER OF OPTIONAL PRODUCTS EVER SOLD INCLUDED IN A SERVICE PACKAGE
/**
  The query computes the average number of optional products ever sold with a given service package for each service package.
  To do so, it uses an additional view which counts the number of optional products included in each PAID order.
  Given the secondary view, this query computes the average optional products ever sold with a ServicePackage simply
  calculating the average value of sales grouping by ServicePackage.
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


-- BEST SELLER OPTIONAL PRODUCT
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