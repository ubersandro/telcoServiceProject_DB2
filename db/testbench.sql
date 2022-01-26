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
