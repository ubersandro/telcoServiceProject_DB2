-- PROCEDURE DEFINITION
/**
  This procedure is provided with an order ID as input parameter and, for each optional product
  included in the Order it just updates the corresponding sales report table.
 */
CREATE OR REPLACE PROCEDURE updateProductSales(IN orderID INT)
BEGIN
    DECLARE currentProduct VARCHAR(45);
    DECLARE done INT DEFAULT 0;


    DECLARE curs CURSOR FOR
        SELECT I.productName AS optionalProduct FROM Includes I WHERE I.orderId = orderId;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    OPEN curs;

    salesUpdateLoop :
    LOOP
        FETCH curs INTO currentProduct;
        IF done THEN
            LEAVE salesUpdateLoop;
        END IF;
        -- UPDATE SALES REPORT TABLE
        UPDATE optionalProduct_sales X SET sales=sales + 1 WHERE X.productName = currentProduct;
    END LOOP;
    CLOSE curs;
END;


CREATE OR REPLACE TRIGGER createServiceActivationSchedule
    AFTER UPDATE
    ON `Order`
    FOR EACH ROW
BEGIN
    IF new.status = '2' THEN -- WHENEVER AN ORDER IS MARKED AS PAID (STATUS = 2)
        INSERT INTO ServiceActivationSchedule(orderID, endDate)
        VALUES (new.id, ADDDATE(new.startingDate, INTERVAL NEW.vpMonths MONTH));
    END IF;
END;

CREATE OR REPLACE TRIGGER updateSalesSPVP
    AFTER INSERT
    ON `Payment`
    FOR EACH ROW
BEGIN
    IF new.status = '0' THEN -- on payment approval
        UPDATE purchasesPerPackageVP
        SET counter=counter + 1
        WHERE servicePackage IN (SELECT O.packageID FROM `Order` O WHERE O.id = NEW.orderID)
          AND validityPeriodMonths IN (SELECT O.vpMonths FROM `Order` O WHERE O.id = NEW.orderID);
    END IF; -- IF THE ORDER WAS PAID
END;

CREATE OR REPLACE TRIGGER updateSalesSPOP
    AFTER INSERT
    ON `Payment`
    FOR EACH ROW
BEGIN
    IF new.status = '0' -- WHENEVER A SUCCESSFUL PAYMENT GOES THROUGH (STATUS = 1)
        AND ((SELECT COUNT(*) FROM Includes I WHERE I.orderId = NEW.orderID GROUP BY I.orderId) >
             '0') THEN -- THE PURCHASE INCLUDES OPTIONAL PRODUCTS
        UPDATE salesSP_OP
        SET purchasesWithOptionalProducts = purchasesWithOptionalProducts + '1',
            totalOptionalProducts         = totalOptionalProducts +
                                            (SELECT COUNT(*)
                                             FROM Includes I
                                             WHERE I.orderId = NEW.orderID
                                             GROUP BY I.orderId)
        WHERE packageID IN (SELECT O.packageID FROM `Order` O WHERE O.id = NEW.orderID);
        -- product sales statistics update
        CALL updateProductSales(new.orderID);
    END IF; -- ORDER PAID
END;



/**
  Whatever the state of a user is, whenever a failed payment is inserted into the payment table
  this trigger marks the user as INSOLVENT.
 */
CREATE OR REPLACE TRIGGER markUserInsolventAfterFailedPayment
    AFTER INSERT
    ON `Payment`
    FOR EACH ROW
BEGIN
    IF NEW.status = 1 -- REJECTED
    THEN
        UPDATE Consumer C
        SET C.status = 1
        WHERE C.username = NEW.user;
    END IF;
END;



/**
  This trigger creates an alert as soon as the third failed payment is inserted into the Payment table.
 */
CREATE OR REPLACE TRIGGER createAlertOnThirdFailedPayment
    AFTER INSERT
    ON Payment
    FOR EACH ROW
BEGIN
    IF NEW.status = 1 AND (SELECT COUNT(*) FROM Payment P WHERE P.user = NEW.user AND P.status = 1) = 3 AND
       NEW.user NOT IN (SELECT A.username FROM Auditing A WHERE A.username = NEW.user) THEN
        INSERT INTO Auditing(username, time, date, email, value)
        VALUES (NEW.user, NEW.time,
                NEW.date, -- ASSUMING THIS IS THE VERY LAST REJECTION
                (SELECT T.email FROM TelcoUser T WHERE T.username = NEW.user),
                NEW.totalValue);
    end if;

END;



CREATE OR REPLACE TRIGGER updateOrderOnPayment -- ON PAYMENT
    AFTER INSERT
    ON `Payment`
    FOR EACH ROW
BEGIN
    IF NEW.status = 1 THEN -- REJECTED
        UPDATE `Order` O SET O.status = 1 WHERE O.id = NEW.orderID;
    ELSE
        IF NEW.status = 0 THEN -- ACCEPTED
            UPDATE `Order` O SET O.status = 2 WHERE O.id = NEW.orderID;
        END IF;
    END IF;
END;


CREATE OR REPLACE TRIGGER setUserSolvent
    AFTER UPDATE
    ON `Order`
    FOR EACH ROW
BEGIN
    IF OLD.status = 1 AND NEW.status = 2 THEN -- order marked as rejected
        IF (SELECT COUNT(*) FROM `Order` O WHERE O.consUsername = NEW.consUsername AND O.status = 1) = 0 THEN
            UPDATE Consumer C SET C.status = 0 WHERE C.username = NEW.consUsername;
        END IF;
    END IF;
END;

CREATE OR REPLACE TRIGGER onServicePackageInsertion
    AFTER INSERT
    ON ServicePackage
    FOR EACH ROW
BEGIN
    INSERT INTO salesSP_OP (packageID, totalOptionalProducts, purchasesWithOptionalProducts) VALUES (NEW.id, 0, 0);
    INSERT INTO purchasesPerPackageVP (servicePackage, validityPeriodMonths)
    VALUES (NEW.id, 12),
           (NEW.id, 24),
           (NEW.id, 36);
END;



CREATE OR REPLACE TRIGGER onOptionalProductInsertion
    AFTER INSERT
    ON OptionalProduct
    FOR EACH ROW
BEGIN
    INSERT INTO optionalProduct_sales(productName, sales) VALUES (NEW.name, 0); -- CONSTRAINTS WILL FAIL
END;

-- PAYMENT STATUS
-- 0 APPROVED
-- 1 REJECTED

-- ORDER STATUS
-- 0 NEWLY_CREATED
-- 1 REJECTED
-- 2 PAID