import json
from flask import Flask, request
import db
import uuid

app = Flask(__name__)
app.debug = True

engine = db.Engine()

def generate_token():
    return uuid.uuid4().hex

@app.route("/login", methods = ["POST"])
def login():
    data = request.get_json(force=True)
    try:
        name = data["name"]
        password = data["password"]
    except:
        return "", 400

    user = engine.get_user(name)

    if not user:
        return "", 401

    if password != user["password"]:
        return "", 401

    token = generate_token()

    result = engine.add_session(user["user_id"], token)

    if not result:
        return "", 500

    envelope = {}
    envelope["token"] = token
    return json.dumps(envelope), 200

@app.route("/logout", methods = ["POST"])
def logout():
    data = request.get_json(force=True)
    try:
        token = data["token"]
    except:
        return "", 400

    result = engine.remove_session(token)

    if not result:
        return "", 401

    return "", 204

@app.route("/register_family", methods = ["POST"])
def register_family():
    data = request.get_json(force=True)
    try:
        name = data["name"]
        email = data["email"]
        password = data["password"]
        family_name = data["family_name"]
    except:
        return "", 400

    result = engine.add_admin(name, email, password, family_name)

    if not result:
        return "", 401

    return "", 204

@app.route("/register_member", methods = ["POST"])
def register_member():
    data = request.get_json(force=True)
    try:
        name = data["name"]
        password = data["password"]
        family_name = data["family_name"]
    except:
        return "", 400

    result = engine.add_member(name, password, family_name)

    if not result:
        return "", 401

    return "", 204

@app.route("/products")
def products():
    try:
        token = request.args.get("token")
    except:
        return "", 400

    products = engine.get_products(token)

    return json.dumps(products), 200

if __name__ == '__main__':
    app.run(debug=True)
