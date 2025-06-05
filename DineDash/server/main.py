

from flask import Flask, jsonify,request
from flask_cors import CORS
# from connection import connect_with_connector
from connection_template import connect_with_connector
from sqlalchemy import text
from sqlalchemy.orm import sessionmaker
from sqlalchemy.exc import SQLAlchemyError
from connection_template import connect_with_connector
from controllers.auth_controller import login_user, add_new_user
from controllers.user_controller import get_user_data, delete_user
from controllers.order_controller import get_orders, get_order_details, delete_order_items, create_or_add_to_order, update_order_quantity
from controllers.restaurants_controller import get_restaurants_by_zipcode, get_popular_data, get_most_popular_restaurants,get_top_50_restaurants,search_restaurants_in_db
from controllers.menu_controller import get_all_items
from helpers import remove_special_characters, process_input_with_apostrophe
from controllers.defaultQuery_controller import get_queries
from controllers.restaurant_filters_controller import filter_by_price_range, filter_by_score, filter_by_rating_count, filter_by_zipcode
app = Flask(__name__)
app.secret_key = 'your_secret_key'

#specify origins here
cors = CORS(app, origins='*',resources={r"/api/*": {"origins": "http://localhost:5173"}})

engine = connect_with_connector()

def createCon():
    Session = sessionmaker(bind=engine)
    session = Session()
    return session

@app.route('/api/login', methods=['POST'])
def login():
    return login_user(request, engine)

@app.route('/api/createaccount', methods=['POST'])
def create_account():
    return add_new_user(request, engine)

@app.route('/api/users/<int:user_id>', methods=['GET'])
def user_data(user_id):
    data = get_user_data(user_id, engine)
    if data:
        return jsonify({"success": True, "data": data})
    else:
        return jsonify({"success": False, "message": "User or orders not found"})

@app.route('/api/delete/<int:user_id>', methods=['DELETE'])
def delete_account(user_id):
    return delete_user(user_id, engine)

@app.route('/api/users/<int:user_id>/orders', methods=['GET'])
def user_orders(user_id):
    return jsonify(get_orders(user_id, engine))

@app.route('/api/users/<int:user_id>/orders/<int:order_id>', methods=['GET'])
def order_details(user_id, order_id):
    order = get_order_details(user_id, order_id, engine)
    if order:
        return jsonify(order)
    return jsonify({"success": False, "message": "Order not found"})

@app.route('/api/users/<int:user_id>/orders/<int:order_id>', methods=['DELETE'])
def delete_order(user_id, order_id):
    return delete_order_items(user_id, order_id, engine)

@app.route('/api/restaurants/<int:zipcode>' , methods=['GET'])
def default_restaurant_by_zip(zipcode):
    restaurant_list = get_restaurants_by_zipcode(zipcode, engine)
    if restaurant_list:
        print(restaurant_list)
        return  jsonify({"success": True, "restaurants": restaurant_list})
    return jsonify({"success": False, "message": "No Restaurants Matching Zipcode"})

@app.route('/api/restaurants/default', methods=['GET'])
def top_50_restaurants():
    restaurant_list = get_top_50_restaurants(engine)
    if restaurant_list:
        return jsonify({"success": True, "restaurants": restaurant_list})
    return jsonify({"success": False, "message": "No Restaurants Found"})

@app.route('/api/menuItems/<int:RestaurantID>/', methods=['GET'])
def restaurant_menu_page(RestaurantID):
    print(f"Received RestaurantID: {RestaurantID}")
    menu_items = get_all_items(RestaurantID, engine)
    print(menu_items)
    if menu_items:
        print(menu_items)
        return  jsonify({"success": True, "menu": menu_items})
    return jsonify({"success": False, "message": "No Menu Items matching Restaurant Id"})

@app.route('/api/searchrestaurants', methods=['POST'])
def search_restaurants():
    data = request.json
    search = data.get('query') if data else None
    if search is None or search == "":
        return jsonify({"success": False, "message": "No restaurant provided"})
    
    search_value = process_input_with_apostrophe(search)
    print(f"Received data: search={search_value}")  # debug line

    restaurant_data = search_restaurants_in_db(search_value, engine)
    
    if restaurant_data is not None:
        return jsonify({"success": True, "message": "Successfully returned restaurant names", "restaurants": restaurant_data})
    return jsonify({"success": False, "message": "An error occurred while searching for restaurants"})

@app.route('/api/restaurants/filter', methods=['POST'])
def get_filtered_restaurants():
    data = request.json
    filters = data.get("filters")
    filter_data = filters.get('restaurantFilters')
    print(f"User Filter Data", filter_data)
    userZip = data.get("userZip")

    prices = filter_data["PriceRange"] if "PriceRange" in filter_data else []
    score = filter_data["Score"] if "Score" in filter_data else {"min": "0", "max": "5"}
    ratings = filter_data["RatingCount"] if "RatingCount" in filter_data else {"min": "0", "max": "500"}
    zipcode = filter_data["ZipCode"] if "ZipCode" in filter_data else ["Any zipcode"]
    # Initialize filtered lists
    restaurant_price_filtered = []
    restaurant_score_filtered = []
    restaurant_rating_filtered = []
    restaurant_zipcode_filtered = []

    if prices != []:
        restaurant_price_filtered = filter_by_price_range(prices, engine)

    min = score["min"]
    max = score["max"]
    restaurant_score_filtered = filter_by_score(min, max, engine)

    min = ratings["min"]
    max = ratings["max"]
    restaurant_rating_filtered = filter_by_rating_count(min, max, engine)

    if zipcode[0] == "Same zipcode":
        restaurant_zipcode_filtered = filter_by_zipcode(userZip, engine)

    # Extract values for the specified key
    price_list = {r["RestaurantID"] for r in restaurant_price_filtered}
    score_list = {r["RestaurantID"] for r in restaurant_score_filtered}
    rating_count_list = {r["RestaurantID"] for r in restaurant_rating_filtered}
    zipcode_list = {r["RestaurantID"] for r in restaurant_zipcode_filtered}
    
    common_restaurants_1 = price_list.intersection(score_list)
    common_restaurants_2 = common_restaurants_1.intersection(rating_count_list)
    if zipcode[0] == "Same zipcode":
        common_restaurants_3 = common_restaurants_2.intersection(zipcode_list)
        common_res = []
        for r in restaurant_price_filtered + restaurant_score_filtered + restaurant_rating_filtered + restaurant_zipcode_filtered:
            if r["RestaurantID"] in common_restaurants_3:
                common_res.append(r)
    else:
        common_res = []
        for r in restaurant_price_filtered + restaurant_score_filtered + restaurant_rating_filtered:
            if r["RestaurantID"] in common_restaurants_2:
                common_res.append(r)
    #return length on common_res
    print(f"Total common results: {len(common_res)}")
    return jsonify({"success": True, "data": common_res}) 

@app.route('/api/restaurants/popular', methods=['GET'])
def most_popular_restaurants():
    return jsonify(get_most_popular_restaurants(engine))


@app.route('/api/popular', methods=['GET'])
def popular_restaurants_and_items():
    return jsonify(get_popular_data(engine))

@app.route('/api/query/<int:idx>', methods=['GET'])
def find_default_query(idx):
    userZip = request.args.get('userZip')
    print(f"UserZip:{userZip}")
    print(f"Query Index: {idx}")
    if userZip is None:
        return jsonify({'success': False, 'error': 'userZip is required', 'data': []})

    query_result = get_queries(idx, userZip, engine)
    
    return jsonify(query_result)

@app.route('/api/createorder', methods=['POST'])
def create_order():
    data = request.json
    user_id = data["user_id"]
    order_details = data["orderDetails"]
    restaurant_id = order_details["RestaurantID"]
    menu_items = order_details["Items"]
    for i in range(len(menu_items)):
        menu_id = menu_items[i]["MenuID"]
        quantity = menu_items[i]["Quantity"]
        action = "add"
        if i == 0:
            action = "create"
        order = create_or_add_to_order(user_id, restaurant_id, menu_id, quantity, action, engine)

    if order == []:
        return jsonify({"success": False, "message": "Order not created"})
    return jsonify({"success": True, "message": "Order created successfully", "data": order})

@app.route('/api/orders/updateorder', methods=['POST'])
def update_order():
    data = request.json

    update_items = data["updatedItems"]["updateItems"]
    for item in update_items:
        order_id = item["OrderID"]
        menu_id = item["MenuID"]
        quantity = item["Quantity"]
        update_order_quantity(order_id, menu_id, quantity, engine)
    return jsonify({"success": True, "message": "Order updated successfully"})
    
if __name__ == "__main__":
    app.run(debug=True, port=5000)
