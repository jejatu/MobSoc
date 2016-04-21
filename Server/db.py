import os, sqlite3

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

        return results, cur.lastrowid
