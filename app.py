import os
import sqlite3

from flask import (Flask, abort, flash, g, redirect, render_template,
                   request, url_for)

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DB_PATH = os.path.join(BASE_DIR, "students.db")
PER_PAGE = 25

DEPARTMENTS = ["Computer Science", "Electronics", "Mechanical", "Civil",
               "Electrical", "Information Technology", "Chemical", "Biotechnology"]

app = Flask(__name__)
app.secret_key = os.environ.get("SECRET_KEY", "change-me-in-production")


def get_db():
    if "db" not in g:
        g.db = sqlite3.connect(DB_PATH)
        g.db.row_factory = sqlite3.Row
    return g.db


@app.teardown_appcontext
def close_db(_exc):
    db = g.pop("db", None)
    if db is not None:
        db.close()


def init_db():
    with sqlite3.connect(DB_PATH) as db:
        with open(os.path.join(BASE_DIR, "schema.sql")) as f:
            db.executescript(f.read())


def read_form():
    f = request.form
    data = {k: f.get(k, "").strip() for k in
            ("roll_no", "name", "email", "phone", "gender", "dob",
             "department", "year", "cgpa", "city", "admission_year")}
    errors = []
    if not data["roll_no"]:
        errors.append("Roll number is required.")
    if not data["name"]:
        errors.append("Name is required.")
    if "@" not in data["email"]:
        errors.append("Enter a valid email address.")
    if data["department"] not in DEPARTMENTS:
        errors.append("Choose a department from the list.")
    try:
        if not 1 <= int(data["year"]) <= 4:
            raise ValueError
    except ValueError:
        errors.append("Year must be a number from 1 to 4.")
    try:
        if data["cgpa"] and not 0 <= float(data["cgpa"]) <= 10:
            raise ValueError
    except ValueError:
        errors.append("CGPA must be a number from 0 to 10.")
    return data, errors


@app.route("/")
def index():
    q = request.args.get("q", "").strip()
    dept = request.args.get("dept", "").strip()
    page = max(request.args.get("page", 1, type=int), 1)

    where, params = [], []
    if q:
        where.append("(name LIKE ? OR roll_no LIKE ? OR email LIKE ?)")
        params += [f"%{q}%"] * 3
    if dept:
        where.append("department = ?")
        params.append(dept)
    clause = ("WHERE " + " AND ".join(where)) if where else ""

    db = get_db()
    total = db.execute(f"SELECT COUNT(*) FROM students {clause}", params).fetchone()[0]
    pages = max((total + PER_PAGE - 1) // PER_PAGE, 1)
    page = min(page, pages)
    rows = db.execute(
        f"SELECT * FROM students {clause} ORDER BY roll_no LIMIT ? OFFSET ?",
        params + [PER_PAGE, (page - 1) * PER_PAGE]).fetchall()
    return render_template("index.html", students=rows, q=q, dept=dept,
                           page=page, pages=pages, total=total,
                           departments=DEPARTMENTS)


@app.route("/add", methods=["GET", "POST"])
def add():
    if request.method == "POST":
        data, errors = read_form()
        if not errors:
            try:
                get_db().execute(
                    "INSERT INTO students (roll_no, name, email, phone, gender, dob,"
                    " department, year, cgpa, city, admission_year)"
                    " VALUES (:roll_no, :name, :email, :phone, :gender, :dob,"
                    " :department, :year, NULLIF(:cgpa, ''), :city, NULLIF(:admission_year, ''))",
                    data)
                get_db().commit()
                flash(f"Added {data['name']}.")
                return redirect(url_for("index"))
            except sqlite3.IntegrityError:
                errors.append(f"Roll number {data['roll_no']} already exists.")
        for e in errors:
            flash(e, "error")
        return render_template("form.html", s=data, departments=DEPARTMENTS, mode="add")
    return render_template("form.html", s={}, departments=DEPARTMENTS, mode="add")


@app.route("/edit/<int:sid>", methods=["GET", "POST"])
def edit(sid):
    db = get_db()
    student = db.execute("SELECT * FROM students WHERE id = ?", (sid,)).fetchone()
    if student is None:
        abort(404)
    if request.method == "POST":
        data, errors = read_form()
        data["id"] = sid
        if not errors:
            try:
                db.execute(
                    "UPDATE students SET roll_no=:roll_no, name=:name, email=:email,"
                    " phone=:phone, gender=:gender, dob=:dob, department=:department,"
                    " year=:year, cgpa=NULLIF(:cgpa, ''), city=:city,"
                    " admission_year=NULLIF(:admission_year, '') WHERE id=:id", data)
                db.commit()
                flash(f"Saved changes to {data['name']}.")
                return redirect(url_for("index"))
            except sqlite3.IntegrityError:
                errors.append(f"Roll number {data['roll_no']} already exists.")
        for e in errors:
            flash(e, "error")
        return render_template("form.html", s=data, departments=DEPARTMENTS, mode="edit", sid=sid)
    return render_template("form.html", s=student, departments=DEPARTMENTS, mode="edit", sid=sid)


@app.route("/delete/<int:sid>", methods=["POST"])
def delete(sid):
    db = get_db()
    db.execute("DELETE FROM students WHERE id = ?", (sid,))
    db.commit()
    flash("Student deleted.")
    return redirect(url_for("index"))


if __name__ == "__main__":
    if not os.path.exists(DB_PATH):
        init_db()
    app.run(debug=True)
