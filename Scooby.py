from bottle import route, run, template
import json


@route('/', method="GET")
def index():
    return template("index.html", root="")


def main():
    run(host='localhost', port=7000)


if __name__ == '__main__':
    main()