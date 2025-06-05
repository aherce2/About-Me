CREATE PROCEDURE ManageOrderTransaction2(
    IN p_UserID INT,
    IN p_RestaurantID INT,
    IN p_MenuID INT,
    IN p_Quantity INT, 
    IN p_Action VARCHAR(10)
)
BEGIN
    DECLARE v_OrderID INT;
    DECLARE v_ItemCount INT;


    SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;


    START TRANSACTION;


    -- Advanced Query 1: Check if an order exists for the given UserID and RestaurantID
    SELECT o.OrderID INTO v_OrderID
    FROM orders o
    JOIN users u ON o.UserID = u.UserID
    WHERE o.UserID = p_UserID AND o.RestaurantID = p_RestaurantID;

    IF p_Action = 'create' THEN
        IF v_OrderID IS NULL THEN
            SELECT MAX(OrderID)+1 INTO v_OrderID FROM orders;
            INSERT INTO orders (OrderID, UserId, RestaurantID)
            VALUES (v_OrderID, p_UserID, p_RestaurantID);
        END IF;
        INSERT INTO orderitems (OrderID, MenuID, Quantity)
        VALUES (v_OrderID, p_MenuID, p_Quantity);


    IF p_Action = 'add' THEN
        IF v_OrderID IS NOT NULL THEN
            -- Advanced Query 3: Use a subquery to get the current quantity
            SELECT Quantity INTO v_ItemCount
            FROM orderitems
            WHERE OrderID = v_OrderID AND MenuID = p_MenuID;
           
            IF v_ItemCount IS NULL THEN
                INSERT INTO orderitems (OrderID, MenuID, Quantity)
                VALUES (v_OrderID, p_MenuID, p_Quantity);
            ELSE
                UPDATE orderitems
                SET Quantity = Quantity + p_Quantity
                WHERE OrderID = v_OrderID AND MenuID = p_MenuID;
            END IF;
        ELSE
            SELECT MAX(OrderID)+1 INTO v_OrderID FROM orders;
            INSERT INTO orders (OrderID, UserId, RestaurantID)
            VALUES (v_OrderID, p_UserID, p_RestaurantID);
            INSERT INTO orderitems (OrderID, MenuID, Quantity)
            VALUES (v_OrderID, p_MenuID, p_Quantity);
        END IF;
    END IF;

    
    -- Advanced Query 4: Get the total number of items in the order
    SELECT oi.OrderID, p_RestaurantID, SUM(oi.Quantity) as totalitems, SUM(m.Price * oi.Quantity) as totalprice
    FROM orderitems oi
    LEFT JOIN menus m ON oi.MenuID = m.MenuID
    WHERE oi.OrderID = v_OrderID
    GROUP BY oi.OrderID;

    COMMIT;
END;
