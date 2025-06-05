# controllers/user_controller.py

from flask import jsonify
from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy import text
from sqlalchemy.orm import sessionmaker

def get_user_data(user_id, engine):
    """Retrieves data for a specific user."""
    print(f"Retrieving data for user_id={user_id}")  # Debug line
    try:
        Session = sessionmaker(bind=engine)
        session = Session()

        user_query = """
        SELECT 
            users.UserID, 
            users.Email, 
            users.zipcode,
            orders.OrderID, 
            restaurants.Name as RestaurantName, 
            menuitems.MenuID, 
            menuitems.Name as MenuItemName, 
            menuitems.Price, 
            orderitems.Quantity
        FROM users 
        LEFT JOIN orders ON users.UserID = orders.UserID
        LEFT JOIN orderitems ON orders.OrderID = orderitems.OrderID
        LEFT JOIN restaurants ON orders.RestaurantID = restaurants.RestaurantID
        LEFT JOIN menuitems ON orderitems.MenuID = menuitems.MenuID
        WHERE users.UserID = :user_id
        """
        user_data = session.execute(text(user_query), {'user_id': user_id})
        result_user_data = [dict(row) for row in user_data.mappings().all()]
        return result_user_data

    except SQLAlchemyError as e:
        print(f"Error: {e}")
        return []

    finally:
        session.close()

def delete_user(user_id, engine):
    """Deletes a user from the database."""
    try:
        Session = sessionmaker(bind=engine)
        session = Session()

        # Begin transaction
        session.begin()

        # Delete the user
        session.execute(text("DELETE FROM users WHERE UserID = :user_id"), {'user_id': user_id})

        # Commit the transaction
        session.commit()

        return jsonify({"success": True, "message": "Account and associated data deleted successfully"})

    except SQLAlchemyError as e:
        session.rollback()
        print(f"Error: {e}")
        return jsonify({"success": False, "message": "An error occurred while deleting the account"})

    finally:
        session.close()
