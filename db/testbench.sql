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
VALUES (@orderId, '2022-01-21', '123456', '200.0', '2022-01-22', 'consumerA', @packageID, '12');
SELECT *
FROM `Order`;
UPDATE `Order`
SET status='1'
WHERE id = @orderId;
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



INSERT INTO TelcoUser(username, email, password, DTYPE) VALUES ('Bill', 'b@b.b', 'b', 'CONS');
INSERT INTO Consumer(username, counter, status) VALUES ('Bill', 1, 1);
UPDATE Consumer C SET C.counter = 0 WHERE C.username='Bill';

