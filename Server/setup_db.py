import db

eng = db.Engine()
eng.remove_database()
eng.create_tables()
eng.populate_tables()
