import json
from flask import Flask, Response

app = Flask(__name__)
app.debug = True

@app.route("/test")
def test():
    envelope = {}
    envelope["made_by"] = "Seppo"
    envelope["message"] = "Hello everybody!"
    envelope["title"] = "Title of the test"
    return json.dumps(envelope)

if __name__ == '__main__':
    app.run(debug=True)
