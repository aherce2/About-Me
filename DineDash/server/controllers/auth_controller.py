# controllers/auth_controller.py

from flask import jsonify
from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy import text
from sqlalchemy.orm import sessionmaker


def login_user(request, engine):
    """Handles user login."""
    data = request.json
    user_id = data.get('user_id')
    email = data.get('email')

    print(f"Received data: user_id={user_id}, email={email}")  # Debug line

    query = text(f"SELECT * FROM users WHERE UserID={user_id} AND Email='{email}'")
    with engine.connect() as conn:
        result = conn.execute(query)
        user = result.fetchone()
        if user:
            return jsonify({"success": True, "message": "Login successful"})
        else:
            return jsonify({"success": False, "message": "Invalid credentials"})

def add_new_user(request, engine):
    """Adds a new user to the database."""
    data = request.json
    email = data.get('email')
    zipcode = data.get('zipcode')
    print(f"Received data: email={email}, zipcode={zipcode}")  # Debug line

    # Validate zipcode
    if len(zipcode) != 5 or not zipcode.isdigit():
        return jsonify({"success": False, "message": "Invalid Zipcode"})

    try:
        Session = sessionmaker(bind=engine)
        session = Session()
        result = session.execute(text(f"SELECT count(*) FROM users WHERE email = :email"), {'email': email})
        count = result.scalar()
        print(f"Count of users with email {email}: {count}")
        
        if count > 0:
            return jsonify({"success": False, "message": "Email already exists"})
        
        # Get the max UserID and increment by 1
        result = session.execute(text(f"SELECT max(UserID) FROM users"))
        maxId = result.scalar()
        print(f"Max Id: {maxId}")
        
        if maxId is None:
            maxId = 1000
        else:
            maxId += 1

        # Insert the new user
        session.execute(
            text("INSERT INTO users (UserId, Email, ZipCode) VALUES (:UserID, :email, :zipCode)"),
            {'UserID': maxId, 'email': email, 'zipCode': zipcode}
        )
        session.commit()

        return jsonify({"success": True, "message": f"Account created. User ID: {maxId}", "UserID": maxId})

    except SQLAlchemyError as e:
        print(f"Error: {e}")
        return jsonify({"success": False, "message": "Error creating account"})

    finally:
        session.close()
