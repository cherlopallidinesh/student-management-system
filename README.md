# Student Management System (Java)

A console application written in plain Java to manage student records.
It needs only the JDK (version 11 or newer) and no other libraries.
It starts with 500 sample students stored in `data/students.csv`.

## Features
- View all students (20 per page)
- Search by name, roll number or email
- Filter by department
- Add, update and delete students (with input checks)
- Statistics: students and average CGPA per department, top 5 by CGPA
- Changes are saved to `data/students.csv`

## Run it
Windows:
```
run.bat
```
Mac / Linux:
```
./run.sh
```
Or by hand:
```
javac -d out src/*.java
java -cp out Main
```
Run these commands from the project folder so the program can find `data/students.csv`.

## Project structure
```
src/Student.java            The student model (fields, CSV conversion)
src/StudentRepository.java  Loads, searches and saves students
src/Main.java               Menu and user input
data/students.csv           500 sample students
```

> The sample data is randomly generated; names, emails and phone numbers are fictional.
