-- Use the schema
SET search_path TO TLP;

-- Table: Course
CREATE TABLE course (
                        id SERIAL PRIMARY KEY,
                        course_name VARCHAR(255),
                        course_code VARCHAR(255),
                        course_description TEXT,
                        course_credit VARCHAR(50)
);

-- Table: Student
CREATE TABLE student (
                         id SERIAL PRIMARY KEY,
                         first_name VARCHAR(255),
                         last_name VARCHAR(255),
                         email VARCHAR(255) UNIQUE,
                         password_hash VARCHAR(255)
);

-- Table: Teacher
CREATE TABLE teacher (
                         id SERIAL PRIMARY KEY,
                         first_name VARCHAR(255),
                         last_name VARCHAR(255),
                         email VARCHAR(255) UNIQUE,
                         encrypted_password VARCHAR(255)
);

-- Join Table: course_student
CREATE TABLE course_student (
                                course_id INTEGER NOT NULL,
                                student_id INTEGER NOT NULL,
                                PRIMARY KEY (course_id, student_id),
                                FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
                                FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
);

-- Join Table: course_teacher
CREATE TABLE course_teacher (
                                course_id INTEGER NOT NULL,
                                teacher_id INTEGER NOT NULL,
                                PRIMARY KEY (course_id, teacher_id),
                                FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
                                FOREIGN KEY (teacher_id) REFERENCES teacher(id) ON DELETE CASCADE
);