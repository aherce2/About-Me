from flask import sessions, jsonify
from sqlalchemy.orm import sessionmaker
from helpers import remove_special_characters
from sqlalchemy import text
from sqlalchemy.exc import SQLAlchemyError

def filter_by_price_range(prices, engine):
    """Retrieves restaurants based on price range"""
    restaurants = []
    try:
        Session = sessionmaker(bind=engine)
        session = Session()

        for price in prices:
            p = price + "\r"
            price_query = f"""
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
            LEFT JOIN menuitems ON restaurants.RestaurantID = menuitems.RestaurantID
            WHERE restaurants.PriceRange = :price
            LIMIT 2000
            """
            price_data = session.execute(text(price_query), {'price': p})
            restaurants.extend([dict(row) for row in price_data.mappings().all()])
        return restaurants

    except SQLAlchemyError as e:
        print(f"Error: {e}")
        return []
    finally:
        session.close()

# Similarly adjust the other filter functions


def filter_by_score(min, max, engine):
    """Retrieves restaurants based on rating score"""
    try:
        Session = sessionmaker(bind=engine)
        session = Session()

        # Query to get restaurants based on rating score
        score_query = """
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
        LEFT JOIN menuitems ON restaurants.RestaurantID = menuitems.RestaurantID
        WHERE ratings.Score BETWEEN :Min AND :Max
        GROUP BY restaurants.RestaurantID, restaurants.Name, restaurants.Category, restaurants.Zipcode, restaurants.PriceRange
        LIMIT 2000
        """
        score_data = session.execute(text(score_query), {'Min': min, 'Max': max})

        retaurants = [dict(row) for row in score_data.mappings().all()]
        return retaurants

    except SQLAlchemyError as e:
        print(f"Error: {e}")
        return []
    finally:
        session.close()

def filter_by_rating_count(min, max, engine):
    """Retrieves restaurants based on rating count"""
    try:
        Session = sessionmaker(bind=engine)
        session = Session()

        # Query to get restaurants based on rating count
        rating_count_query = """
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
        LEFT JOIN menuitems ON restaurants.RestaurantID = menuitems.RestaurantID
        WHERE ratings.Ratings BETWEEN :Min AND :Max
        GROUP BY restaurants.RestaurantID, restaurants.Name, restaurants.Category, restaurants.Zipcode, restaurants.PriceRange
        LIMIT 2000
        """
        rating_count_data = session.execute(text(rating_count_query), {'Min': min, 'Max': max})

        restaurants = [dict(row) for row in rating_count_data.mappings().all()]
        return restaurants
    except SQLAlchemyError as e:
        print(f"Error: {e}")
        return []
    finally:
        session.close()

def filter_by_zipcode(zipcode, engine):
    """Retrieves restaurants based on zipcode"""
    print(f"zipcode: {zipcode}")
    restaurants = []
    try:
        Session = sessionmaker(bind=engine)
        session = Session()

        # Query to get restaurants based on zipcode
        zipcode_query = f"""
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
        LEFT JOIN menuitems ON restaurants.RestaurantID = menuitems.RestaurantID
        WHERE restaurants.ZipCode = {zipcode}
        GROUP BY restaurants.RestaurantID, restaurants.Name, restaurants.Category, restaurants.Zipcode, restaurants.PriceRange
        LIMIT 2000
        """
        zipcode_data = session.execute(text(zipcode_query))
        restaurants.extend([dict(row) for row in zipcode_data.mappings().all()])
        print(restaurants)
        return restaurants
    except SQLAlchemyError as e:
        print(f"Error: {e}")
        return []
    finally:
        session.close()

