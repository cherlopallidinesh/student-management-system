CREATE TABLE IF NOT EXISTS students (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    roll_no        TEXT NOT NULL UNIQUE,
    name           TEXT NOT NULL,
    email          TEXT NOT NULL,
    phone          TEXT,
    gender         TEXT,
    dob            TEXT,
    department     TEXT NOT NULL,
    year           INTEGER NOT NULL CHECK (year BETWEEN 1 AND 4),
    cgpa           REAL CHECK (cgpa BETWEEN 0 AND 10),
    city           TEXT,
    admission_year INTEGER
);
