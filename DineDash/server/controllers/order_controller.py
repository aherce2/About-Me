# controllers/order_controller.py

from flask import sessions, jsonify
from sqlalchemy.orm import sessionmaker
from helpers import remove_special_characters
from sqlalchemy import text
from sqlalchemy.exc import SQLAlchemyError

def get_orders(user_id, engine):
    """Retrieves all orders for a specific user."""
    try:
        Session = sessionmaker(bind=engine)
        session = Session()
        
        # Get user orders
        orders_query = """
        SELECT 
            orders.OrderID, 
            restaurants.Name as RestaurantName, 
            menuitems.Name as MenuItemName, 
            menuitems.Price, 
            orderitems.Quantity,
            orderitems.MenuID
        FROM orders
        LEFT JOIN orderitems ON orders.OrderID = orderitems.OrderID
        LEFT JOIN restaurants ON orders.RestaurantID = restaurants.RestaurantID
        LEFT JOIN menuitems ON orderitems.MenuID = menuitems.MenuID
        WHERE orders.UserID = :user_id
        """
        order_data = session.execute(text(orders_query), {'user_id': user_id})
        
        # Process the order data into a structured format
        user_order_data = [dict(row) for row in order_data.mappings().all()]

        orders = []
        order_dict = {}
        for data in user_order_data:
            order_id = data["OrderID"]

            # Initialize the order if not already present
            if order_id not in order_dict:
                order_dict[order_id] = {
                    "OrderID": order_id,
                    "Restaurant": remove_special_characters(data["RestaurantName"]),
                    "Items": [],
                    "TotalPrice": 0.0
                }
            
            item_price = float(data["Price"].split(" ")[0])
            item_total_price = item_price * data["Quantity"]
            
            order_dict[order_id]["Items"].append({
                "MenuItem": remove_special_characters(data["MenuItemName"]),
                "MenuID": data["MenuID"],
                "Quantity": data["Quantity"],
                "Price": item_price
            })
            order_dict[order_id]["TotalPrice"] += item_total_price
        
        orders = list(order_dict.values())
        return orders

    except SQLAlchemyError as e:
        print(f"Error: {e}")
        return []

    finally:
        session.close()


def get_order_details(user_id, order_id, engine):
    """Retrieves details of a specific order for a user."""
    orders = get_orders(user_id, engine)
    order = next((order for order in orders if order["OrderID"] == order_id), None)
    return order

def delete_order_items(user_id, order_id, engine):
    try:
        Session = sessionmaker(bind=engine)
        session = Session()

        # Begin transaction
        session.begin()

        # Delete the user
        session.execute(
            text("DELETE FROM orders WHERE UserID = :user_id AND OrderID = :order_id"),
            {'user_id': user_id, 'order_id': order_id}
        )
        # Commit the transaction
        session.commit()

        return jsonify({"success": True, "message": "Account and associated data deleted successfully"})

    except SQLAlchemyError as e:
        session.rollback()
        print(f"Error: {e}")
        return jsonify({"success": False, "message": "An error occurred while deleting the account"})

    finally:
        session.close()

def create_or_add_to_order(user_id, restaurant_id, menu_id, quantity, action, engine):
    try:
        Session = sessionmaker(bind=engine)
        session = Session()

        query = "CALL ManageOrderTransaction2(:p_UserID, :p_RestaurantID, :p_MenuID, :p_Quantity, :p_Action)"
        update_order = session.execute(text(query), {"p_UserID": user_id, "p_RestaurantID": restaurant_id, "p_MenuID": menu_id, "p_Quantity": quantity, "p_Action": action})
        order_details = [dict(row) for row in update_order.mappings().all()]

        return order_details

    except SQLAlchemyError as e:
        session.rollback()
        print(f"Error: {e}")
        return []

    finally:
        session.close()

def update_order_quantity(order_id, menu_id, new_quantity, engine):
    try:
        Session = sessionmaker(bind=engine)
        session = Session()

        query = "UPDATE orderitems SET Quantity = :new_quantity WHERE OrderID = :order_id and MenuID = :menu_id;"
        session.execute(text(query), {"new_quantity": new_quantity, "order_id": order_id, "menu_id": menu_id})
        session.commit()

        return jsonify({"success": True, "message": "Order updated successfully"})

    except SQLAlchemyError as e:
        session.rollback()
        print(f"Error: {e}")
        return jsonify({"success": False, "message": "An error occurred while updating the order"})

    finally:
        session.close()