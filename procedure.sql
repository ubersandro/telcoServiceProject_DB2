
CREATE OR REPLACE PROCEDURE updateProductSales(IN orderID INT)
BEGIN
    DECLARE currentProduct VARCHAR(45);
    DECLARE done INT DEFAULT 0;


    DECLARE curs CURSOR FOR
        SELECT I.productName AS optionalProduct FROM Includes I WHERE I.orderId = orderId;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    OPEN curs;

    salesUpdateLoop : LOOP
        FETCH curs INTO currentProduct;
        IF done THEN
            LEAVE salesUpdateLoop;
        END IF;
        -- UPDATE SALES REPORT TABLE

        IF ((SELECT count(*) FROM optionalProduct_sales S WHERE S.productName=currentProduct) != '0') THEN
            UPDATE optionalProduct_sales X SET sales=sales+1 WHERE X.productName=currentProduct;
        ELSE -- THE TUPLE DOES NOT EXIST
            INSERT INTO optionalProduct_sales(productName, sales) VALUES (currentProduct, '1');
        END IF;


    END LOOP;
    CLOSE curs ;
END

