--  QUERIES
/**
  These queries have to be included (in JPQL) in the business logic
  of the application to retrieve statistics to be displayed in the
  sales report page.
 */

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
SELECT S.packageID AS ServicePackage,
       sum(S.totalOptionalProducts) / (SELECT sum(counter)
                                       FROM purchasesPerPackageVP X
                                       WHERE X.servicePackage = S.packageID
                                       GROUP BY X.servicePackage) AS "Average number of optional products included"
FROM salesSP_OP S
GROUP BY S.packageID;

-- BEST SELLER OPTIONAL PRODUCT -> PRODUCT SOLD THE MAXIMUM NUMBER OF TIMES -> NON UNIQUE !
SELECT *
FROM optionalProduct_sales S
WHERE S.sales = (SELECT DISTINCT MAX(ops.sales) FROM optionalProduct_sales ops);
