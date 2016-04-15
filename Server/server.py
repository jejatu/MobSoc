import json
from flask import Flask, Response

app = Flask(__name__)
app.debug = True

@app.route("/test")
def test():
    return "Test response!"

if __name__ == '__main__':
    app.run(debug=True)
