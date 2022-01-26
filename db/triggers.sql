
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
    END IF ;
END;

CREATE OR REPLACE TRIGGER updateSalesSPVP
    AFTER UPDATE
    ON `Order`
    FOR EACH ROW
BEGIN
    IF new.status = '2' THEN -- WHENEVER AN ORDER IS MARKED AS PAID (STATUS = 2)
        IF ((SELECT COUNT(*) FROM purchasesPerPackageVP P WHERE P.servicePackage = NEW.packageID AND P.validityPeriodMonths = NEW.vpMonths) = '0')
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

CREATE OR REPLACE TRIGGER setInsolventUser
    AFTER UPDATE
    ON `Order`
    FOR EACH ROW
BEGIN
    -- an order is set as rejected and the SOLVENT user has to be marked as insolvent and the counter set from 0 to 1.
    -- if the user is already insolvent the counter is incremented by 1
    -- if the order was already rejected the user counter is incremented anyway because another payment failed
    IF NEW.status = 1 -- REJECTED
    THEN
        UPDATE Consumer C
        SET C.status = 1, C.counter = C.counter + 1
        WHERE C.username = NEW.consUsername;
    END IF;
END;



/*CREATE VIEW lastRejectedOrders(user, date, time, value) AS
SELECT O.consUsername AS user, O.date AS date, O.time AS time, O.totalValue AS value
FROM `Order` O
WHERE O.date IN (SELECT date FROM dateLastRejection D WHERE D.consumer = O.consUsername)
GROUP BY O.consUsername
HAVING max(O.time);

CREATE VIEW dateLastRejection(consumer, date) AS
SELECT O.consUsername AS consumer, MAX(O.date) AS date
FROM `Order` O
WHERE O.status = 1
GROUP BY O.consUsername;

CREATE OR REPLACE TRIGGER createAlert -- alert is created but never removed
    AFTER UPDATE
    ON Consumer
    FOR EACH ROW
BEGIN
    --  if counter reaches 3 and the user has no alerts pending then put an alert in the auditing table
    IF NEW.counter = 3 AND NEW.username NOT IN (SELECT A.username FROM Auditing A WHERE A.username = NEW.username) THEN
        INSERT INTO Auditing(username, time, date, email, value)
        VALUES (NEW.username, (SELECT l.time FROM lastRejectedOrders l LIMIT 1),
                (SELECT l.date FROM lastRejectedOrders l LIMIT 1),
                (SELECT T.email FROM TelcoUser T WHERE T.username = NEW.username),
                (SELECT l.value FROM lastRejectedOrders l LIMIT 1));
    END IF;
END;
*/

/**
  This trigger must fire only AFTER the counter of rejected payments of a given user has been incremented.
  If the new value of the counter is 3 and no alerts for the given user exist, then an alert is created.
 */
CREATE OR REPLACE TRIGGER createAlert -- V2
    AFTER UPDATE ON `Order`
    FOR EACH ROW
    FOLLOWS setInsolventUser
    BEGIN
        IF NEW.status = 1 AND (SELECT C.counter FROM Consumer C WHERE C.username = NEW.consUsername ) = 3 AND
            NEW.consUsername NOT IN (SELECT A.username FROM Auditing A WHERE A.username = NEW.consUsername) THEN
             INSERT INTO Auditing(username, time, date, email, value)
        VALUES (NEW.consUsername, NEW.time,
                NEW.date, -- ASSUMING THIS IS THE VERY LAST REJECTION
                (SELECT T.email FROM TelcoUser T WHERE T.username = NEW.consUsername),
                NEW.totalValue);
        end if;

    END;

/**
  If a rejected order is paid, the counter of the associated user is decremented. If this latter reaches the value 0, the
  user is no longer insolvent.
 */
CREATE OR REPLACE TRIGGER updateConsumerOnPayment -- ON PAYMENT
    AFTER UPDATE
    ON `Order`
    FOR EACH ROW
BEGIN
    IF OLD.status = 1 AND NEW.status = 2 THEN
        IF (SELECT * FROM `Order` O WHERE O.consUsername = NEW.consUsername AND O.status=1) = 0 THEN
            UPDATE  Consumer C SET C.status = 0 WHERE C.username = NEW.consUsername;
        END IF;
    END IF;
END;



