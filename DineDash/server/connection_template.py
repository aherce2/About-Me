from google.cloud.sql.connector import Connector, IPTypes
import pymysql

import sqlalchemy


def connect_with_connector() -> sqlalchemy.engine.base.Engine:
    """
    Initializes a connection pool for a Cloud SQL instance of Postgres.

    Uses the Cloud SQL Python Connector package.
    """
    # Note: Saving credentials in environment variables is convenient, but not
    # secure - consider a more secure solution such as
    # Cloud Secret Manager (https://cloud.google.com/secret-manager) to help
    # keep secrets safe.
    # https://us-central1-cosmic-abbey-429902-a5.cloudfunctions.net/connect_with_connector
    instance_connection_name = "cosmic-abbey-429902-a5:us-central1:db-dine-dash" # e.g. 'project:region:instance' -> copy connection name from sql instance on gcp
    db_user = "root"  # e.g. 'my-db-user' -> I was using root as my own user was not working
    db_pass = "cs411"  # e.g. 'my-db-password'
    db_name = "dine_dash"  # e.g. 'my-database'

    ip_type = IPTypes.PUBLIC

    # initialize Cloud SQL Python Connector object
    connector = Connector()

    def getconn() -> pymysql.connections.Connection:
        conn: pymysql.connections.Connection = connector.connect(
            instance_connection_name,
            "pymysql",
            user=db_user,
            password=db_pass,
            db=db_name,
            ip_type=ip_type,
        )
        return conn

    # The Cloud SQL Python Connector can be used with SQLAlchemy
    # using the 'creator' argument to 'create_engine'
    pool = sqlalchemy.create_engine(
        "mysql+pymysql://",
        creator=getconn,
        # ...
    )
    return pool

engine = connect_with_connector()

