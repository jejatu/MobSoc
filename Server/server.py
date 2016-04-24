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
        family_name = data["family_name"]
        password = data["password"]
    except:
        return "", 400

    user = engine.get_user(name, family_name)

    if not user:
        return "", 401

    if password != user["password"]:
        return "", 401

    token = generate_token()

    result = engine.add_session(user, token)

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

@app.route("/me", methods = ["GET"])
def me():
    try:
        token = request.args.get("token")
    except:
        return "", 400

@app.route("/products", methods = ["GET"])
def get_products():
    try:
        token = request.args.get("token")
    except:
        return "", 400

    products = engine.get_products(token)

    envelope = {}
    envelope["products"] = products;

    return json.dumps(envelope), 200

@app.route("/products", methods = ["POST"])
def add_product():
    data = request.get_json(force=True)
    try:
        token = request.args.get("token")
        name = data["name"]
        description = data["description"]
    except:
        return "", 400

    result = engine.add_product(token, name, description)

    if not result:
        return "", 500

    return "", 204

if __name__ == '__main__':
    app.run(debug=True)
