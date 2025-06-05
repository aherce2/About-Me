from flask import sessions, jsonify
from sqlalchemy.orm import sessionmaker
from helpers import remove_special_characters
from sqlalchemy import text
from sqlalchemy.exc import SQLAlchemyError

def get_all_items(RestaurantID, engine):
    """Retrieves menuItems"""
    try:
        Session = sessionmaker(bind=engine)
        session = Session()

        # Query to get restaurants and their ratings in the same zipcode
        menu_query = """
        SELECT 
            menuitems.MenuID,
            menuitems.Name as MenuName,
            menuitems.Category,
            menuitems.Description,
            menuitems.Price,
            menuitems.Vegetarian,
            menuitems.Halal
            FROM menuitems
            LEFT JOIN restaurants ON menuitems.RestaurantID = restaurants.RestaurantID
            WHERE menuitems.RestaurantID = :RestaurantID
        """
        menu_data = session.execute(text(menu_query), {'RestaurantID': RestaurantID})
        print(menu_data)
        # Process the restaurant data into a structured format
        menu_info = [dict(row) for row in menu_data.mappings().all()]

        return menu_info

    except SQLAlchemyError as e:
        print(f"Error: {e}")
        return []

    finally:
        session.close()