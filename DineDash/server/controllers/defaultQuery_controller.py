from sqlalchemy.orm import sessionmaker
from sqlalchemy import text
from sqlalchemy.exc import SQLAlchemyError

def get_queries(idx, userZip, engine):
    """
    Runs a stored procedure to get data based on the given idx and userZip.
    Returns the query results in a structured format.
    """
    try:
        Session = sessionmaker(bind=engine)
        session = Session()

        # Call the stored procedure with idx and userZip
        query = text("CALL dine_dash.GetDataByID(:idx, :userZip)")
        print(query)
        result = session.execute(query, {"idx": idx, "userZip": userZip})

        # Process the query data into a structured format
        result_list = [dict(row) for row in result.mappings().all()]

        # # Add type field based on idx
        # for item in result_list:
        #     item['type'] = 'Restaurant' if idx in [0, 2, 3, 4] else 'Menu'

        return {
            'success': True,
            'data': result_list
        }

    except SQLAlchemyError as e:
        print(f"Error: {e}")
        return {
            'success': False,
            'error': str(e),
            'data': []
        }

    finally:
        session.close()