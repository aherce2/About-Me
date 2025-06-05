CREATE TRIGGER DEL_USER
	BEFORE DELETE ON users
	FOR EACH ROW
	BEGIN 
	IF EXISTS (SELECT * FROM orders WHERE orders.UserID = old.UserID)
    THEN
        DELETE FROM orderitems WHERE orderitems.OrderID IN (SELECT OrderID FROM orders WHERE orders.UserID = old.UserID);
        DELETE FROM orders WHERE orders.UserID = old.UserID;
    END IF;
END;