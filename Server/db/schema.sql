PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS users(
  user_id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT,
  email TEXT,
  password TEXT,
  role INTEGER,
  status INTEGER,
  UNIQUE(user_id),
  UNIQUE(name)
);

CREATE TABLE IF NOT EXISTS sessions(
  token TEXT PRIMARY KEY,
  user_id INTEGER,
  add_date TEXT,
  FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  UNIQUE(token),
  UNIQUE(user_id)
);

CREATE TABLE IF NOT EXISTS families(
 family_id INTEGER PRIMARY KEY AUTOINCREMENT,
 family_name TEXT,
 UNIQUE(family_id, family_name)
);

CREATE TABLE IF NOT EXISTS user_families(
  family_id INTEGER,
  user_id INTEGER,
  FOREIGN KEY(family_id) REFERENCES families(family_id) ON DELETE CASCADE,
  FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  PRIMARY KEY(family_id, user_id)
);

CREATE TABLE IF NOT EXISTS products(
  product_id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT,
  description TEXT,
  adder TEXT,
  add_date TEXT,
  image_url TEXT,
  family_id INTEGER,
  FOREIGN KEY(family_id) REFERENCES families(family_id) ON DELETE CASCADE,
  UNIQUE(product_id)
);

COMMIT;
PRAGMA foreign_keys=ON;
