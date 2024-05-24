CREATE SEQUENCE user_seq START 41;
CREATE SEQUENCE instructor_seq START 21;
CREATE SEQUENCE student_seq START 21;
CREATE SEQUENCE lesson_seq START 21;
CREATE SEQUENCE announcement_seq START 1;
CREATE SEQUENCE task_seq START 21;
CREATE SEQUENCE answer_task_seq START 21;
CREATE SEQUENCE comment_seq START 21;
CREATE SEQUENCE course_seq START 21;
CREATE SEQUENCE group_seq START 21;
CREATE SEQUENCE link_seq START 21;
CREATE SEQUENCE notification_seq START 21;
CREATE SEQUENCE option_seq START 21;
CREATE SEQUENCE presentation_seq START 21;
CREATE SEQUENCE question_seq START 21;
CREATE SEQUENCE res_test_seq START 21;
CREATE SEQUENCE test_seq START 21;
CREATE SEQUENCE trash_seq START 1;

CREATE TABLE users (
                       id BIGINT PRIMARY KEY DEFAULT nextval('user_seq'),
                       full_name VARCHAR(255),
                       email VARCHAR(255) UNIQUE,
                       password VARCHAR(255),
                       role VARCHAR(255),
                       phone_number VARCHAR(255),
                       block BOOLEAN,
                       uuid VARCHAR(255),
                       removed_date DATE,
                       type VARCHAR(255)
);

CREATE TABLE instructors (
                             id BIGINT PRIMARY KEY DEFAULT nextval('instructor_seq'),
                             specialization VARCHAR(255),
                             created_at DATE,
                             updated_at DATE,
                             type VARCHAR(255),
                             user_id BIGINT UNIQUE,
                             trash_id BIGINT,
                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                             FOREIGN KEY (trash_id) REFERENCES trash(id)
);

CREATE TABLE students (
                          id BIGINT PRIMARY KEY DEFAULT nextval('student_seq'),
                          study_format VARCHAR(255),
                          created_at DATE,
                          updated_at DATE,
                          type VARCHAR(255),
                          user_id BIGINT UNIQUE,
                          group_id BIGINT,
                          trash_id BIGINT,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          FOREIGN KEY (group_id) REFERENCES groups(id),
                          FOREIGN KEY (trash_id) REFERENCES trash(id)
);

CREATE TABLE lessons (
                         id BIGINT PRIMARY KEY DEFAULT nextval('lesson_seq'),
                         title VARCHAR(255),
                         created_at DATE,
                         updated_at DATE,
                         course_id BIGINT,
                         FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE announcements (
                               id BIGINT PRIMARY KEY DEFAULT nextval('announcement_seq'),
                               announcement_content TEXT,
                               is_published BOOLEAN,
                               published_date DATE,
                               expiration_date DATE,
                               user_id BIGINT,
                               FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE tasks (
                       id BIGINT PRIMARY KEY DEFAULT nextval('task_seq'),
                       title VARCHAR(255),
                       description TEXT,
                       file VARCHAR(255),
                       image VARCHAR(255),
                       code TEXT,
                       deadline TIMESTAMP,
                       created_at DATE,
                       updated_at DATE,
                       lesson_id BIGINT,
                       trash_id BIGINT,
                       FOREIGN KEY (lesson_id) REFERENCES lessons(id),
                       FOREIGN KEY (trash_id) REFERENCES trash(id)
);

CREATE TABLE answer_tasks (
                              id BIGINT PRIMARY KEY DEFAULT nextval('answer_task_seq'),
                              text TEXT,
                              image VARCHAR(255),
                              file VARCHAR(255),
                              task_answer_status VARCHAR(255),
                              point INT,
                              date_of_send TIMESTAMP,
                              updated_at TIMESTAMP,
                              student_id BIGINT,
                              task_id BIGINT,
                              FOREIGN KEY (student_id) REFERENCES students(id),
                              FOREIGN KEY (task_id) REFERENCES tasks(id)
);

CREATE TABLE comments (
                          id BIGINT PRIMARY KEY DEFAULT nextval('comment_seq'),
                          content TEXT,
                          created_at TIMESTAMP,
                          user_id BIGINT,
                          answer_task_id BIGINT,
                          FOREIGN KEY (user_id) REFERENCES users(id),
                          FOREIGN KEY (answer_task_id) REFERENCES answer_tasks(id)
);

CREATE TABLE courses (
                         id BIGINT PRIMARY KEY DEFAULT nextval('course_seq'),
                         title VARCHAR(255),
                         description TEXT,
                         image VARCHAR(255),
                         date_of_start DATE,
                         date_of_end DATE,
                         type VARCHAR(255),
                         trash_id BIGINT,
                         FOREIGN KEY (trash_id) REFERENCES trash(id)
);

CREATE TABLE groups (
                        id BIGINT PRIMARY KEY DEFAULT nextval('group_seq'),
                        title VARCHAR(255),
                        description TEXT,
                        image VARCHAR(255),
                        date_of_start DATE,
                        date_of_end DATE,
                        removed_date DATE,
                        trash_id BIGINT,
                        FOREIGN KEY (trash_id) REFERENCES trash(id)
);

CREATE TABLE links (
                       id BIGINT PRIMARY KEY DEFAULT nextval('link_seq'),
                       title VARCHAR(255),
                       url VARCHAR(255),
                       lesson_id BIGINT,
                       trash_id BIGINT,
                       video_id BIGINT,
                       FOREIGN KEY (lesson_id) REFERENCES lessons(id),
                       FOREIGN KEY (trash_id) REFERENCES trash(id),
                       FOREIGN KEY (video_id) REFERENCES videos(id)
);

CREATE TABLE notifications (
                               id BIGINT PRIMARY KEY DEFAULT nextval('notification_seq'),
                               title VARCHAR(255),
                               description TEXT,
                               created_at DATE,
                               task_id BIGINT,
                               answer_task_id BIGINT,
                               FOREIGN KEY (task_id) REFERENCES tasks(id),
                               FOREIGN KEY (answer_task_id) REFERENCES answer_tasks(id)
);

CREATE TABLE options (
                         id BIGINT PRIMARY KEY DEFAULT nextval('option_seq'),
                         option TEXT,
                         is_true BOOLEAN,
                         question_id BIGINT,
                         FOREIGN KEY (question_id) REFERENCES questions(id)
);

CREATE TABLE presentations (
                               id BIGINT PRIMARY KEY DEFAULT nextval('presentation_seq'),
                               title VARCHAR(255),
                               description TEXT,
                               file VARCHAR(255),
                               created_at DATE,
                               updated_at DATE,
                               trash_id BIGINT,
                               FOREIGN KEY (trash_id) REFERENCES trash(id)
);

CREATE TABLE questions (
                           id BIGINT PRIMARY KEY DEFAULT nextval('question_seq'),
                           title VARCHAR(255),
                           question_type VARCHAR(255),
                           point DOUBLE PRECISION,
                           test_id BIGINT,
                           FOREIGN KEY (test_id) REFERENCES tests(id)
);

CREATE TABLE result_tests (
                              id BIGINT PRIMARY KEY DEFAULT nextval('res_test_seq'),
                              point DOUBLE PRECISION,
                              creation_date DATE,
                              update_date DATE,
                              test_id BIGINT,
                              student_id BIGINT,
                              FOREIGN KEY (test_id) REFERENCES tests(id),
                              FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE TABLE tests (
                       id BIGINT PRIMARY KEY DEFAULT nextval('test_seq'),
                       title VARCHAR(255),
                       is_active BOOLEAN,
                       creation_date DATE,
                       hour INT,
                       minute INT,
                       lesson_id BIGINT,
                       trash_id BIGINT,
                       FOREIGN KEY (lesson_id) REFERENCES lessons(id),
                       FOREIGN KEY (trash_id) REFERENCES trash(id)
);

CREATE TABLE trash (
                       id BIGINT PRIMARY KEY DEFAULT nextval('trash_seq'),
                       name VARCHAR(255),
                       type VARCHAR(255),
                       date_of_delete TIMESTAMP
);

CREATE TABLE video (
                       id BIGINT PRIMARY KEY,
                       url VARCHAR(255)
);

-- Many-to-Many Relationship between Instructors and Courses
CREATE TABLE instructors_courses (
                                     instructor_id BIGINT,
                                     course_id BIGINT,
                                     PRIMARY KEY (instructor_id, course_id),
                                     FOREIGN KEY (instructor_id) REFERENCES instructors(id),
                                     FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- Many-to-Many Relationship between Groups and Courses
CREATE TABLE groups_courses (
                                group_id BIGINT,
                                course_id BIGINT,
                                PRIMARY KEY (group_id, course_id),
                                FOREIGN KEY (group_id) REFERENCES groups(id),
                                FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- Many-to-Many Relationship between Announcements and Groups
CREATE TABLE announcements_groups (
                                      announcement_id BIGINT,
                                      group_id BIGINT,
                                      PRIMARY KEY (announcement_id, group_id),
                                      FOREIGN KEY (announcement_id) REFERENCES announcements(id),
                                      FOREIGN KEY (group_id) REFERENCES groups(id)
);

