import json
from flask import Flask, request, send_file
from werkzeug import secure_filename
import db
import uuid
import os

UPLOAD_FOLDER = 'images'
ALLOWED_EXTENSIONS = set(['jpg'])

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
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

    user = engine.get_user_by_token(token)

    if not user:
        return 500

    envelope = {}
    envelope["name"] = user["name"]
    envelope["family_name"] = user["family_name"]
    return json.dumps(envelope), 200

@app.route("/members", methods = ["GET"])
def get_members():
    try:
        token = request.args.get("token")
    except:
        return "", 400

    members = engine.get_members(token)

    envelope = {}
    envelope["members"] = members;

    return json.dumps(envelope), 200

@app.route("/members", methods = ["POST"])
def accept_member():
    data = request.get_json(force=True)
    try:
        token = request.args.get("token")
        member_id = data["member_id"]
    except:
        return "", 400

    result = engine.accept_member(token, member_id)

    if not result:
        return "", 500

    return "", 204

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

    product_id = engine.add_product(token, name, description)

    if not product_id:
        return "", 500

    envelope = {}
    envelope["product_id"] = product_id;

    return json.dumps(envelope), 200

@app.route("/purchase", methods = ["POST"])
def purchase():
    data = request.get_json(force=True)
    try:
        token = request.args.get("token")
        product_id = data["product_id"]
    except:
        return "", 400

    results = engine.set_purchased(token, product_id)

    if not results:
        return "", 500

    return "", 204

def allowed_file(filename):
    return '.' in filename and \
        filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS

@app.route('/image/<product_id>', methods=['POST'])
def image(product_id):
    try:
        token = request.args.get("token")
    except:
        return "", 400

    if not engine.has_product(token, product_id):
        return "", 500

    name = product_id + ".jpg"

    file = request.files['uploaded_file']
    if file and allowed_file(name):
        filename = secure_filename(name)
        file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
        return "", 204
    return "", 400

@app.route('/image/<product_id>', methods=['GET'])
def get_image(product_id):
    try:
        token = request.args.get("token")
    except:
        return "", 400

    if not engine.has_product(token, product_id):
        return "", 500

    filename = os.path.join(app.config['UPLOAD_FOLDER'], product_id + ".jpg")
    if os.path.isfile(filename):
        return send_file(filename, mimetype='image/gif')
    return "", 404

if __name__ == '__main__':
    app.run(debug=True)
