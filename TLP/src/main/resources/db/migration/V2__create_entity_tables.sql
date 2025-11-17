SET search_path TO TLP;

CREATE TABLE courses (
                         course_id SERIAL PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         description TEXT
);

CREATE TABLE subcategories (
                               subcategory_id SERIAL PRIMARY KEY,
                               course_id INTEGER NOT NULL,
                               title VARCHAR(255) NOT NULL,
                               description TEXT,
                               FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

CREATE TABLE modules (
                         module_id SERIAL PRIMARY KEY,
                         subcategory_id INTEGER NOT NULL,
                         title VARCHAR(255) NOT NULL,
                         content TEXT,
                         FOREIGN KEY (subcategory_id) REFERENCES subcategories(subcategory_id) ON DELETE CASCADE,
                         created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE teacher (
                         id SERIAL PRIMARY KEY,
                         first_name VARCHAR(255),
                         last_name VARCHAR(255),
                         email VARCHAR(255) UNIQUE,
                         password_hash VARCHAR(255)
);
