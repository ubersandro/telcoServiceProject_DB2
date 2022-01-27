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

        IF ((SELECT count(*) FROM optionalProduct_sales S WHERE S.productName = currentProduct) != '0') THEN
            UPDATE optionalProduct_sales X SET sales=sales + 1 WHERE X.productName = currentProduct;
        ELSE -- THE TUPLE DOES NOT EXIST
            INSERT INTO optionalProduct_sales(productName, sales) VALUES (currentProduct, '1');
        END IF;


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
    AFTER UPDATE
    ON `Order`
    FOR EACH ROW
BEGIN
    IF new.status = '2' THEN -- WHENEVER AN ORDER IS MARKED AS PAID (STATUS = 2)
        IF ((SELECT COUNT(*)
             FROM purchasesPerPackageVP P
             WHERE P.servicePackage = NEW.packageID
               AND P.validityPeriodMonths = NEW.vpMonths) = '0')
        THEN -- THERE NOT EXISTS A TUPLE WITH THE GIVEN PACKAGE (NOR WITH THE CORRESPONDENT VALIDITY PERIODS)
            INSERT INTO purchasesPerPackageVP(servicePackage, validityPeriodMonths, counter)
            VALUES (NEW.packageID, NEW.vpMonths, 1);
        ELSE -- THERE EXISTS AT LEAST A TUPLE WITH THE GIVEN SERVICE PACKAGE AND THE GIVEN VALIDITY PERIOD
            UPDATE purchasesPerPackageVP
            SET counter=counter + 1
            WHERE servicePackage = NEW.packageID
              AND validityPeriodMonths = NEW.vpMonths;
        END IF;
    END IF; -- IF THE ORDER WAS PAID
END;

CREATE OR REPLACE TRIGGER updateSalesSPOP
    AFTER UPDATE
    ON `Order`
    FOR EACH ROW
BEGIN
    IF new.status = '2' THEN -- WHENEVER AN ORDER IS MARKED AS PAID (STATUS = 2)
        IF ((SELECT COUNT(*) FROM Includes I WHERE I.orderId = NEW.id) > '0') THEN -- THE PURCHASE INCLUDES OPTIONAL PRODUCTS
            IF ((SELECT COUNT(*) FROM salesSP_OP P WHERE P.packageID = NEW.packageID) > '0') -- THE TUPLE EXISTS
            THEN
                UPDATE salesSP_OP
                SET purchasesWithOptionalProducts = purchasesWithOptionalProducts + '1',
                    totalOptionalProducts         = totalOptionalProducts +
                                                    (SELECT COUNT(*) FROM Includes I WHERE I.orderId = NEW.id)
                WHERE packageID = NEW.packageID;
            ELSE -- THE TUPLE DOES NOT EXIST
                INSERT INTO salesSP_OP(packageID, totalOptionalProducts, purchasesWithOptionalProducts)
                VALUES (NEW.packageID, (SELECT COUNT(*) FROM Includes I WHERE I.orderId = NEW.id), 1);
            END IF;
            -- product sales statistics update
            CALL updateProductSales(new.id);
        END IF; -- purchase with optional products
    END IF; -- ORDER PAID
END;

CREATE OR REPLACE TRIGGER updateSalesSPOP
    AFTER UPDATE
    ON `Order`
    FOR EACH ROW
BEGIN
    IF new.status = '2' THEN -- WHENEVER AN ORDER IS MARKED AS PAID (STATUS = 2)
        IF ((SELECT COUNT(*) FROM Includes I WHERE I.orderId = NEW.id) > '0') THEN -- THE PURCHASE INCLUDES OPTIONAL PRODUCTS
            IF ((SELECT COUNT(*) FROM salesSP_OP P WHERE P.packageID = NEW.packageID) > '0') -- THE TUPLE EXISTS
            THEN
                UPDATE salesSP_OP
                SET purchasesWithOptionalProducts = purchasesWithOptionalProducts + '1',
                    totalOptionalProducts         = totalOptionalProducts +
                                                    (SELECT COUNT(*) FROM Includes I WHERE I.orderId = NEW.id)
                WHERE packageID = NEW.packageID;
           ELSE --  PURCHASE INCLUDES OPTIONAL PRODUCTS BUT THE TUPLE DOES NOT EXIST
                INSERT INTO salesSP_OP(packageID, totalOptionalProducts, purchasesWithOptionalProducts)
                VALUES (NEW.packageID, (SELECT COUNT(*) FROM Includes I WHERE I.orderId = NEW.id), 1);
            END IF;
            -- product sales statistics update
            CALL updateProductSales(new.id);
             ELSE  -- THE ORDER DOES NOT INCLUDE OPT PRODS BUT YOU PUT A SERVICE PACKAGE IN ANYWAYS
                 IF (SELECT COUNT(*) FROM salesSP_OP S WHERE S.packageID = NEW.packageID) = 0 THEN -- if the SP is not there add a tuple !
                     INSERT INTO salesSP_OP(packageID, totalOptionalProducts, purchasesWithOptionalProducts) VALUES (NEW.packageID, 0,0);
                 END IF ;
        END IF; -- purchase with optional products
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
        SET C.status  = 1
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
    IF NEW.status = 1 AND (SELECT COUNT(*) FROM Payment P WHERE P.user = NEW.user AND P.status=1) = 3 AND
       NEW.user NOT IN (SELECT A.username FROM Auditing A WHERE A.username = NEW.user) THEN  -- WE COUNT PAYMENTS!
        INSERT INTO Auditing(username, time, date, email, value)
        VALUES (NEW.user, NEW.time,
                NEW.date, -- ASSUMING THIS IS THE VERY LAST REJECTION
                (SELECT T.email FROM TelcoUser T WHERE T.username = NEW.user),
                NEW.totalValue);
    end if;

END;




CREATE OR REPLACE TRIGGER updateOrderOnPayment -- ON PAYMENT
    AFTER INSERT -- FIRST TRIGGER TO FIRE
    ON `Payment`
    FOR EACH ROW
BEGIN
    IF NEW.status = 1 THEN
        UPDATE `Order` O SET O.status = 1 WHERE O.id = NEW.orderID;
    ELSE
        IF NEW.status = 0 THEN
            UPDATE `Order` O SET O.status = 2 WHERE O.id = NEW.orderID;
        END IF;
    END IF;
END;


CREATE OR REPLACE TRIGGER setUserSolvent
    AFTER UPDATE -- LAST ONE TO FIRE
    ON `Order`
    FOR EACH ROW
BEGIN
    IF OLD.status = 1 AND NEW.status = 2 THEN -- order marked as rejected
        IF (SELECT COUNT(*) FROM `Order` O WHERE O.consUsername = NEW.consUsername AND O.status = 1) = 0 THEN
            UPDATE Consumer C SET C.status = 0 WHERE C.username = NEW.consUsername;
        END IF;
    END IF;
END;

