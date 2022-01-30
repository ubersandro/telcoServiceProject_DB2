use telcoServiceDB;
-- preliminary cleaning
DELETE FROM optionalProduct_sales ;
DELETE FROM ServiceActivationSchedule ;
DELETE FROM salesSP_OP ;
DELETE FROM purchasesPerPackageVP;
DELETE FROM `Order`;

-- test bench
SET @orderId = 4;
SET @packageID = 2; -- it includes opt1, opt2. Package 1 includes them as well.
INSERT INTO `Order`(ID, DATE, TIME, TOTALVALUE, STARTINGDATE, CONSUSERNAME, PACKAGEID, VPMONTHS)
VALUES (@orderId, '2022-01-21', '123456', '200.0', '2022-01-22', 'A', @packageID, '12');
SELECT *
FROM `Order`;
INSERT INTO Payment( user, orderID, date, time, totalValue, status)
VALUES ('A', @orderId, '2022-12-12', '12:30:00', '200.00', 0);


SELECT *
FROM `Order`;

-- include opts in the order
INSERT INTO Includes (orderId, productName)
VALUES (@orderId, 'opt1');

SELECT *
FROM Includes I
WHERE I.orderId = @orderId;
-- ORDER PAYMENT -> check purchases with optional products
UPDATE `Order`
SET status='2'
WHERE id = @orderId;
SELECT *
FROM ServiceActivationSchedule;
SELECT *
FROM purchasesPerPackageVP;
SELECT *
FROM salesSP_OP;
SELECT *
FROM optionalProduct_sales;


SELECT HV.packageID AS PACKAGE, HV.validityMonths AS VALIDITYPERIOD, O.id/*, COUNT (O.id) AS SALES*/
FROM HasValidity HV LEFT OUTER JOIN `Order` O ON O.packageID AND O.vpMonths = HV.validityMonths
WHERE   O.status = 2;
/*GROUP BY HV.packageID, HV.validityMonths;*/

SELECT SP.id AS PACKAGE,
       (SELECT count(DISTINCT(O.id))
       FROM `Order` O INNER JOIN Includes I ON O.id = I.orderId
       WHERE O.packageID = SP.id AND O.status = 2) AS SALESWOPT
FROM ServicePackage SP;

SELECT SP.packageID, totalOptionalProducts/SP.purchasesWithOptionalProducts
FROM salesSP_OP SP;

SELECT SP.id, avg(TMP.products)
FROM ServicePackage SP, TMP
WHERE SP.id = TMP.package
GROUP BY  SP.id;

CREATE VIEW TMP(ord, package, products) AS
SELECT O.id, O.packageID, COUNT(I.productName)
FROM `Order` O LEFT OUTER JOIN Includes I ON O.id = I.orderId
WHERE O.status = 2
GROUP BY O.id;