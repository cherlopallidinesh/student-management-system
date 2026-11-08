# Student Management System

A small web app to manage student records, built with Python, Flask and SQLite.
It comes with 500 sample students so you can try it right away.

## Features
- List, search (name, roll number, email) and filter by department
- Add, edit and delete students with input validation
- Pagination (25 students per page)
- 500 generated sample records, also provided as `data/students.csv`

## Run it
```bash
python -m venv .venv
source .venv/bin/activate        # Windows: .venv\Scripts\activate
pip install -r requirements.txt
python seed.py                   # creates students.db with 500 students
python app.py                    # open http://127.0.0.1:5000
```

Re-create the sample data at any time with `python seed.py --reset`.

## Project structure
```
app.py            Flask routes and database helpers
schema.sql        Table definition
seed.py           Generates the 500 sample students
data/students.csv The same 500 records as a CSV file
templates/        HTML pages
static/style.css  Styling
```

## Student fields
Roll number, name, email, phone, gender, date of birth, department, year,
CGPA, city, admission year.

> The sample data is randomly generated; names, emails and phone numbers are fictional.
