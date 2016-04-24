import os, sqlite3, traceback
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
            traceback.print_exc()
            con.close()
            return {"data": [], "id": -1}

        results = cur.fetchall()

        con.commit()
        con.close()

        return {"data": results, "id": cur.lastrowid}

    def parse_users(self, results):
        users = []
        for result in results:
            user = {}
            user["user_id"] = str(result[0])
            user["name"] = result[1]
            user["family_name"] = result[2]
            user["email"] = result[3]
            user["password"] = result[4]
            user["role"] = str(result[5])
            user["activated"] = str(result[6])
            users.append(user)
        return users

    def parse_session(self, results):
        sessions = []
        for result in results:
            session = {}
            session["token"] = result[0]
            session["user_id"] = str(result[1])
            session["family_id"] = str(result[2])
            session["add_date"] = result[3]
            sessions.append(session)
        return sessions

    def parse_families(self, results):
        families = []
        for result in results:
            family = {}
            family["family_id"] = str(result[0])
            family["name"] = result[1]
            families.append(family)
        return families

    def parse_products(self, results):
        products = []
        for result in results:
            product = {}
            product["name"] = result[1]
            product["description"] = result[2]
            product["adder"] = result[3]
            product["add_date"] = result[4]
            product["image_url"] = result[5]
            products.append(product)
        return products

    def get_user(self, name, family_name):
        results = self.execute_sql("SELECT * FROM users WHERE name=? AND family_name=?", (name, family_name,))["data"]

        if len(results) == 0:
            return None

        return self.parse_users(results)[0]

    def get_products(self, token):
        results = self.execute_sql("SELECT * FROM products WHERE family_id = \
                                    (SELECT family_id FROM sessions WHERE token = ?)", (token,))["data"]
        return self.parse_products(results)

    def add_session(self, user, token):
        self.execute_sql("DELETE FROM sessions WHERE user_id=?", (user["user_id"],))

        family_data = self.execute_sql("SELECT * FROM families WHERE family_name=?", (user["family_name"],))["data"]
        if len(family_data) == 0:
            return None

        family_id = self.parse_families(family_data)[0]["family_id"]

        date = str(datetime.now())
        results = self.execute_sql("INSERT INTO sessions VALUES(?, ?, ?, ?)", (token, user["user_id"], family_id, date))

        if not results:
            return None

        return results["id"]

    def remove_session(self, token):
        self.execute_sql("DELETE FROM sessions WHERE token=?", (token,))

    def add_admin(self, name, email, password, family_name):
        family_data = self.execute_sql("SELECT * FROM families WHERE family_name=?", (family_name,))["data"]
        if len(family_data) != 0:
            return None
        family_id = self.execute_sql("INSERT INTO families VALUES(NULL, ?)", (family_name,))["id"]
        user_id = self.execute_sql("INSERT INTO users VALUES(NULL, ?, ?, ?, ?, 0, 1)", (name, family_name, email, password))["id"]
        return user_id

    def add_member(self, name, password, family_name):
        family_data = self.execute_sql("SELECT * FROM families WHERE family_name=?", (family_name,))["data"]
        if len(family_data) == 0:
            return None
        family_id = self.parse_families(family_data)[0]["family_id"]
        user_id = self.execute_sql("INSERT INTO users VALUES(NULL, ?, ?, NULL, ?, 1, 0)", (name, family_name, password))["id"]
        if user_id == -1:
            return None
        return user_id

    def add_product(self, token, name, description):
        user_data = self.execute_sql("SELECT * FROM users WHERE user_id = \
                                (SELECT user_id FROM sessions WHERE token = ?)", (token,))["data"]
        if len(user_data) == 0:
            return None
        user = self.parse_users(user_data)[0]
        family_data = self.execute_sql("SELECT * FROM families WHERE family_id = \
                                (SELECT family_id FROM sessions WHERE token = ?)", (token,))["data"]
        if len(family_data) == 0:
            return None
        family = self.parse_families(family_data)[0]
        date = str(datetime.now())
        image_url = "test.jpg"
        product_id = self.execute_sql("INSERT INTO products VALUES(NULL, ?, ?, ?, ?, ?, ?)", (name, description, user["name"], date, image_url, family["family_id"]))["id"]
        return product_id
