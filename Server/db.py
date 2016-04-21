import os, sqlite3
from datetime import datetime

DEFAULT_DB_PATH = "db/db.db"
DEFAULT_DB_SCHEMA = "db/schema.sql"
DEFAULT_DB_DATA = "db/data.sql"

class Engine():
    def __init__(self, path=DEFAULT_DB_PATH):
        '''
        Initializes the engine
        '''
        self.path = path

    def connect(self):
        '''
        Creates a connection to the current database that the engine uses
        '''
        return sqlite3.connect(self.path)

    def remove_database(self):
        '''
        Removes the database from the filesystem
        '''
        if os.path.exists(self.path):
            os.remove(self.path)

    def create_tables(self):
        '''
        Creates the tables according to a schema file
        '''
        con = sqlite3.connect(self.path)
        try:
            with open(DEFAULT_DB_SCHEMA) as f:
                sql = f.read()
                cur = con.cursor()
                cur.executescript(sql)
        finally:
            con.close()

    def populate_tables(self):
        '''
        Populates the tables according to a data file
        '''
        con = sqlite3.connect(self.path)

        cur = con.cursor()
        cur.execute('PRAGMA foreign_keys = ON')

        with open(DEFAULT_DB_DATA) as f:
            sql = f.read()
            cur = con.cursor()
            cur.executescript(sql)

    def execute_sql(self, sql, parameters=()):
        '''
        Opens connection to the database, executes the sql, commits changes and closes the connection
        Returns the results of the sql query and the last row id
        '''
        con = sqlite3.connect(self.path)

        cur = con.cursor()
        try:
            cur.execute(sql, parameters)
        except:
            con.close()
            return None

        results = cur.fetchall()

        con.commit()
        con.close()

        return {"data": results, "id": cur.lastrowid}

    def parse_families(self, results):
        families = []
        for result in results:
            family = {}
            family["family_id"] = str(result[0])
            family["name"] = result[1]
            families.append(family)
        return families

    def parse_users(self, results):
        users = []
        for result in results:
            user = {}
            user["user_id"] = str(result[0])
            user["name"] = result[1]
            user["email"] = result[2]
            user["password"] = result[3]
            user["role"] = str(result[4])
            user["status"] = str(result[5])
            users.append(user)
        return users

    def parse_session(self, results):
        sessions = []
        for result in results:
            session = {}
            session["token"] = result[0]
            session["user_id"] = str(result[1])
            session["add_date"] = result[2]
            sessions.append(session)
        return sessions

    def parse_products(self, results):
        products = []
        for result in results:
            product = {}
            product["product_id"] = str(result[0])
            product["name"] = result[1]
            product["description"] = result[2]
            product["add_date"] = result[3]
            product["image_url"] = result[4]
            product["family_id"] = str(result[5])
            products.append(product)
        return products

    def get_user(self, name):
        results = self.execute_sql("SELECT * FROM users WHERE name=?", (name,))["data"]

        if len(results) == 0:
            return None

        return self.parse_users(results)[0]

    def get_products(self, token):
        results = self.execute_sql("SELECT * FROM products WHERE family_id = \
                                    (SELECT family_id FROM user_families WHERE user_id = \
                                    (SELECT user_id FROM sessions WHERE token = ?))", (token,))["data"]
        return self.parse_products(results)

    def add_session(self, user_id, token):
        date = str(datetime.now())
        session_id = self.execute_sql("INSERT INTO sessions VALUES(?, ?, ?)", (token, user_id, date))["id"]
        return session_id

    def remove_session(self, token):
        self.execute_sql("DELETE FROM sessions WHERE token=?", (token,))

    def add_admin(self, name, email, password, family_name):
        try:
            family_data = self.execute_sql("SELECT * FROM families WHERE family_name=?", (family_name,))["data"]
            if len(family_data) != 0:
                return None
            family_id = self.execute_sql("INSERT INTO families VALUES(NULL, ?)", (family_name,))["id"]
            user_id = self.execute_sql("INSERT INTO users VALUES(NULL, ?, ?, ?, 0, 0)", (name, email, password))["id"]
            self.execute_sql("INSERT INTO user_families VALUES(?, ?)", (family_id, user_id))
        except:
            return None
        return user_id

    def add_member(self, name, password, family_name):
        try:
            family_id = self.execute_sql("SELECT * FROM families WHERE family_name=?", (family_name,))["id"]
            if family_id == 0:
                return None
            user_id = self.execute_sql("INSERT INTO users VALUES(NULL, ?, NULL, ?, 1, 0)", (name, password))["id"]
            self.execute_sql("INSERT INTO user_families VALUES(?, ?)", (family_id, user_id))
        except:
            return None
        return user_id
