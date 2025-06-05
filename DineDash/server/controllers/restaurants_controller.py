from flask import sessions, jsonify
from sqlalchemy.orm import sessionmaker
from helpers import remove_special_characters, process_input_with_apostrophe
from sqlalchemy import text
from sqlalchemy.exc import SQLAlchemyError

def get_restaurants_by_zipcode(user_zipcode, engine):
    """Retrieves information from restaurants and ratings tables for restaurants within the same zipcode as the user."""
    try:
        Session = sessionmaker(bind=engine)
        session = Session()

        # Query to get restaurants and their ratings in the same zipcode
        restaurants_query = """
        SELECT 
            restaurants.RestaurantID,
            restaurants.Name as RestaurantName,
            restaurants.Category,
            restaurants.Zipcode,
            restaurants.PriceRange,
            ratings.Score,
            ratings.Ratings
            FROM restaurants
            LEFT JOIN ratings ON restaurants.RestaurantID = ratings.RestaurantID
            WHERE restaurants.Zipcode = :zipcode
        """
        restaurant_data = session.execute(text(restaurants_query), {'zipcode': user_zipcode})
        print(restaurant_data)
        # Process the restaurant data into a structured format
        restaurant_info = [dict(row) for row in restaurant_data.mappings().all()]

        return restaurant_info

    except SQLAlchemyError as e:
        print(f"Error: {e}")
        return []

    finally:
        session.close()
def get_top_50_restaurants(engine):
    """Retrieves the top 50 restaurants based on rating."""
    try:
        Session = sessionmaker(bind=engine)
        session = Session()

        # Query to get the top 50 restaurants by rating
        top_50_query = """
        SELECT 
            restaurants.RestaurantID,
            restaurants.Name as RestaurantName,
            restaurants.Category,
            restaurants.Zipcode,
            restaurants.PriceRange,
            ratings.Score,
            ratings.Ratings
            FROM restaurants
            LEFT JOIN ratings ON restaurants.RestaurantID = ratings.RestaurantID
            GROUP BY restaurants.RestaurantID, restaurants.Name, restaurants.Category, restaurants.Zipcode, restaurants.PriceRange
            LIMIT 50
        """
        top_50_data = session.execute(text(top_50_query))
        # Process the restaurant data into a structured format
        top_50_info = [dict(row) for row in top_50_data.mappings().all()]

        return top_50_info

    except SQLAlchemyError as e:
        print(f"Error: {e}")
        return []

    finally:
        session.close()
def search_restaurants_in_db(search_value, engine):
    """Searches for restaurants based on the provided search value."""
    try:
        Session = sessionmaker(bind=engine)
        session = Session()

        # Query to search for restaurants by name
        search_query = """
        SELECT 
            restaurants.RestaurantID,
            restaurants.Name as RestaurantName,
            restaurants.Category,
            restaurants.Zipcode,
            restaurants.PriceRange,
            ratings.Score,
            ratings.Ratings
            FROM restaurants
            LEFT JOIN ratings ON restaurants.RestaurantID = ratings.RestaurantID
            WHERE Name LIKE :search_value
            LIMIT 100
        """
        result = session.execute(text(search_query), {'search_value': f'%{search_value}%'})
        restaurant_data = result.mappings().all()

        # Process the restaurant data into a structured format
        return [dict(row) for row in restaurant_data]

    except SQLAlchemyError as e:
        print(f"Error: {e}")
        return None

    finally:
        session.close()
# def get_filtered_restaurants(restaurantId, engine):
#     try:
#         Session = sessionmaker(bind=engine)
#         session = Session()

#         # Query to get restaurants and their ratings in the same zipcode
#         restaurants_query = """
#         SELECT 
#             restaurants.RestaurantID,
#             restaurants.Name as RestaurantName,
#             restaurants.Category,
#             restaurants.Zipcode,
#             restaurants.PriceRange,
#             ratings.Score,
#             ratings.Ratings
#             FROM restaurants
#             LEFT JOIN ratings ON restaurants.RestaurantID = ratings.RestaurantID
#             WHERE restaurants.RestaurantID = :RestaurantID
#         """
#         restaurant_data = session.execute(text(restaurants_query), {'zipcode': restaurantId})
#         print(restaurant_data)
#         # Process the restaurant data into a structured format
#         restaurant_info = [dict(row) for row in restaurant_data.mappings().all()]

#         return restaurant_info

#     except SQLAlchemyError as e:
#         print(f"Error: {e}")
#         return []

#     finally:
#         session.close()

def get_most_popular_restaurants(engine):
    """Runs a stored procedure that calculates the popularity score for each restaurant and returns the most popular restaurants."""
    try:
        Session = sessionmaker(bind=engine)
        session = Session()

        # Query to get the most popular restaurants
        popular_query = "Call PopularityScores();"
        popular_data = session.execute(text(popular_query))
        print(popular_data)
        # Process the restaurant data into a structured format
        popular_info = [dict(row) for row in popular_data.mappings().all()]

        return popular_info

    except SQLAlchemyError as e:
        print(f"Error: {e}")
        return []

    finally:
        session.close()

def get_popular_data(engine):
    """Runs a stored procedure that calculates the popularity score for each restaurant and returns the most popular restaurants."""
    try:
        # Query to get the most popular restaurants
        popular_query = "Call GetPopularRestandItems();"
        conn = engine.raw_connection()
        cursor = conn.cursor()
        popular_data = cursor.execute(popular_query)

        # Process the restaurant data
        columns = [desc[0] for desc in cursor.description]
        popular_info1 = [dict(zip(columns, row)) for row in cursor.fetchall()]
        if cursor.nextset():
            columns = [desc[0] for desc in cursor.description]
            popular_info2 = [dict(zip(columns, row)) for row in cursor.fetchall()]

        return {"popular_restaurants": popular_info1, "popular_items": popular_info2}

    except SQLAlchemyError as e:
        print(f"Error: {e}")
        return []
