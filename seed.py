"""Fill the database with 500 sample students and write data/students.csv.

Usage:  python seed.py          (skips if the table already has data)
        python seed.py --reset  (wipes the table and re-seeds)
"""
import csv
import os
import random
import sqlite3
import sys
from datetime import date, timedelta

from app import DB_PATH, DEPARTMENTS, init_db

COUNT = 500
random.seed(42)

FIRST_M = ["Aarav", "Vihaan", "Arjun", "Rohan", "Karthik", "Sai", "Aditya", "Rahul", "Vikram",
           "Manoj", "Pranav", "Nikhil", "Harsha", "Varun", "Suresh", "Ravi", "Kiran", "Ajay",
           "Naveen", "Deepak", "Imran", "Faizan", "Tarun", "Gautam", "Mohit"]
FIRST_F = ["Ananya", "Diya", "Priya", "Sneha", "Kavya", "Meera", "Pooja", "Ishita", "Lakshmi",
           "Divya", "Shruti", "Neha", "Swathi", "Harini", "Anjali", "Sana", "Riya", "Bhavana",
           "Nandini", "Tanvi", "Aisha", "Keerthi", "Sowmya", "Pallavi", "Mounika"]
LAST = ["Sharma", "Reddy", "Rao", "Naidu", "Patel", "Kumar", "Singh", "Gupta", "Iyer", "Nair",
        "Verma", "Joshi", "Khan", "Das", "Mehta", "Chowdary", "Goud", "Yadav", "Menon", "Pillai",
        "Shetty", "Bhat", "Agarwal", "Mishra", "Ahmed"]
CITIES = ["Hyderabad", "Secunderabad", "Warangal", "Vijayawada", "Visakhapatnam", "Chennai",
          "Bengaluru", "Pune", "Mumbai", "Delhi", "Kolkata", "Nizamabad", "Karimnagar", "Guntur",
          "Tirupati", "Khammam"]
CODES = {"Computer Science": "CS", "Electronics": "EC", "Mechanical": "ME", "Civil": "CE",
         "Electrical": "EE", "Information Technology": "IT", "Chemical": "CH", "Biotechnology": "BT"}


def make_students():
    rows, seen = [], set()
    counters = {d: 0 for d in DEPARTMENTS}
    while len(rows) < COUNT:
        gender = random.choice(["Male", "Female"])
        first = random.choice(FIRST_M if gender == "Male" else FIRST_F)
        last = random.choice(LAST)
        dept = random.choice(DEPARTMENTS)
        year = random.randint(1, 4)
        admission = 2026 - year
        counters[dept] += 1
        roll = f"{str(admission)[2:]}{CODES[dept]}{counters[dept]:03d}"
        name = f"{first} {last}"
        email = f"{first}.{last}.{roll}@college.edu".lower()
        if roll in seen:
            continue
        seen.add(roll)
        age = 17 + year + random.randint(0, 2)
        dob = date(2026 - age, 1, 1) + timedelta(days=random.randint(0, 364))
        rows.append((roll, name, email, f"9{random.randint(100000000, 999999999)}", gender,
                     dob.isoformat(), dept, year, round(random.uniform(5.0, 9.9), 2),
                     random.choice(CITIES), admission))
    rows.sort()
    return rows


def main():
    init_db()
    db = sqlite3.connect(DB_PATH)
    if "--reset" in sys.argv:
        db.execute("DELETE FROM students")
    elif db.execute("SELECT COUNT(*) FROM students").fetchone()[0]:
        print("Table already has data. Use --reset to re-seed.")
        return
    rows = make_students()
    db.executemany(
        "INSERT INTO students (roll_no, name, email, phone, gender, dob, department,"
        " year, cgpa, city, admission_year) VALUES (?,?,?,?,?,?,?,?,?,?,?)", rows)
    db.commit()
    os.makedirs("data", exist_ok=True)
    with open("data/students.csv", "w", newline="") as f:
        w = csv.writer(f)
        w.writerow(["roll_no", "name", "email", "phone", "gender", "dob", "department",
                    "year", "cgpa", "city", "admission_year"])
        w.writerows(rows)
    print(f"Inserted {len(rows)} students and wrote data/students.csv")


if __name__ == "__main__":
    main()
