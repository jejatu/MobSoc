import json
from flask import Flask, request
import db

app = Flask(__name__)
app.debug = True

engine = db.Engine()

def generate_token():
    return 0

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

    envelope = {}
    envelope["token"] = generate_token()
    return json.dumps(envelope), 200

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

if __name__ == '__main__':
    app.run(debug=True)
