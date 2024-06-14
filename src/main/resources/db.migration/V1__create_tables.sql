CREATE SEQUENCE IF NOT EXISTS announcement_seq START WITH 21 INCREMENT BY 1;

CREATE TABLE announcements
(
    id                   BIGINT NOT NULL,
    announcement_content VARCHAR(255),
    is_published         BOOLEAN,
    published_date       date,
    expiration_date      date,
    user_id              BIGINT,
    CONSTRAINT pk_announcements PRIMARY KEY (id)
);

CREATE TABLE announcements_groups
(
    courses_id BIGINT NOT NULL,
    groups_id  BIGINT NOT NULL
);


CREATE SEQUENCE IF NOT EXISTS answer_task_seq START WITH 21 INCREMENT BY 1;

CREATE TABLE answer_tasks
(
    id                 BIGINT  NOT NULL,
    text               VARCHAR(255),
    image              VARCHAR(255),
    file               VARCHAR(255),
    task_answer_status VARCHAR(255),
    point              INTEGER NOT NULL,
    date_of_send       TIMESTAMP WITHOUT TIME ZONE,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    student_id         BIGINT,
    task_id            BIGINT,
    link_id            BIGINT,
    CONSTRAINT pk_answer_tasks PRIMARY KEY (id)
);


CREATE TABLE comments
(
    id             BIGINT NOT NULL,
    content        VARCHAR(255),
    user_id        BIGINT,
    answer_task_id BIGINT,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);


CREATE SEQUENCE IF NOT EXISTS course_seq START WITH 21 INCREMENT BY 1;

CREATE TABLE courses
(
    id            BIGINT NOT NULL,
    title         VARCHAR(255),
    description   VARCHAR(255),
    image         VARCHAR(255),
    date_of_start date,
    date_of_end   date,
    trash_id      BIGINT,
    CONSTRAINT pk_courses PRIMARY KEY (id)
);

CREATE TABLE courses_groups
(
    courses_id BIGINT NOT NULL,
    groups_id  BIGINT NOT NULL
);


CREATE SEQUENCE IF NOT EXISTS exam_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE exams
(
    id         BIGINT NOT NULL,
    title      VARCHAR(255),
    exam_date  date,
    updated_at date,
    course_id  BIGINT,
    CONSTRAINT pk_exams PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS exam_result_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE exam_results
(
    id         BIGINT  NOT NULL,
    student_id BIGINT  NOT NULL,
    exam_id    BIGINT  NOT NULL,
    point      INTEGER NOT NULL,
    CONSTRAINT pk_exam_results PRIMARY KEY (id)
);


CREATE SEQUENCE IF NOT EXISTS group_seq START WITH 21 INCREMENT BY 1;

CREATE TABLE groups
(
    id            BIGINT NOT NULL,
    title         VARCHAR(255),
    description   VARCHAR(255),
    image         VARCHAR(255),
    date_of_start date,
    date_of_end   date,
    trash_id      BIGINT,
    CONSTRAINT pk_groups PRIMARY KEY (id)
);


CREATE SEQUENCE IF NOT EXISTS instructor_seq START WITH 21 INCREMENT BY 1;

CREATE TABLE instructors
(
    id             BIGINT NOT NULL,
    specialization VARCHAR(255),
    created_at     date,
    updated_at     date,
    user_id        BIGINT,
    trash_id       BIGINT,
    CONSTRAINT pk_instructors PRIMARY KEY (id)
);

CREATE TABLE instructors_courses
(
    courses_id     BIGINT NOT NULL,
    instructors_id BIGINT NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS lesson_seq START WITH 21 INCREMENT BY 1;

CREATE TABLE lessons
(
    id         BIGINT NOT NULL,
    title      VARCHAR(255),
    created_at date,
    updated_at date,
    course_id  BIGINT NOT NULL,
    trash_id   BIGINT,
    CONSTRAINT pk_lessons PRIMARY KEY (id)
);

CREATE TABLE lessons_presentations
(
    lesson_id        BIGINT NOT NULL,
    presentations_id BIGINT NOT NULL
);

CREATE TABLE lessons_videos
(
    lesson_id BIGINT NOT NULL,
    videos_id BIGINT NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS link_seq START WITH 30 INCREMENT BY 1;

CREATE TABLE links
(
    id        BIGINT NOT NULL,
    title     VARCHAR(255),
    url       VARCHAR(255),
    lesson_id BIGINT,
    task_id   BIGINT,
    trash_id  BIGINT,
    video_id  BIGINT,
    CONSTRAINT pk_links PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS notification_seq START WITH 21 INCREMENT BY 1;

CREATE TABLE notifications
(
    id             BIGINT NOT NULL,
    title          VARCHAR(255),
    description    VARCHAR(255),
    created_at     date,
    task_id        BIGINT,
    answer_task_id BIGINT,
    CONSTRAINT pk_notifications PRIMARY KEY (id)
);


CREATE SEQUENCE IF NOT EXISTS option_seq START WITH 21 INCREMENT BY 1;

CREATE TABLE options
(
    id          BIGINT NOT NULL,
    option      VARCHAR(255),
    is_true     BOOLEAN,
    question_id BIGINT,
    CONSTRAINT pk_options PRIMARY KEY (id)
);


CREATE SEQUENCE IF NOT EXISTS presentation_seq START WITH 21 INCREMENT BY 1;

CREATE TABLE presentations
(
    id          BIGINT NOT NULL,
    title       VARCHAR(255),
    description VARCHAR(255),
    file        VARCHAR(255),
    created_at  date,
    updated_at  date,
    trash_id    BIGINT,
    lesson_id   BIGINT,
    CONSTRAINT pk_presentations PRIMARY KEY (id)
);


CREATE SEQUENCE IF NOT EXISTS question_seq START WITH 21 INCREMENT BY 1;

CREATE TABLE questions
(
    id            BIGINT           NOT NULL,
    title         VARCHAR(255),
    question_type VARCHAR(255),
    point         DOUBLE PRECISION NOT NULL,
    test_id       BIGINT,
    CONSTRAINT pk_questions PRIMARY KEY (id)
);


CREATE SEQUENCE IF NOT EXISTS res_test_seq START WITH 21 INCREMENT BY 1;

CREATE TABLE result_tests
(
    id            BIGINT           NOT NULL,
    point         DOUBLE PRECISION NOT NULL,
    creation_date date,
    update_date   date,
    test_id       BIGINT,
    student_id    BIGINT,
    CONSTRAINT pk_result_tests PRIMARY KEY (id)
);

CREATE TABLE result_tests_options
(
    result_test_id BIGINT NOT NULL,
    options_id     BIGINT NOT NULL
);


CREATE SEQUENCE IF NOT EXISTS student_seq START WITH 21 INCREMENT BY 1;

CREATE TABLE students
(
    id           BIGINT NOT NULL,
    study_format VARCHAR(255),
    created_at   date,
    updated_at   date,
    type         SMALLINT,
    user_id      BIGINT,
    group_id     BIGINT NOT NULL,
    trash_id     BIGINT,
    CONSTRAINT pk_students PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS task_seq START WITH 21 INCREMENT BY 1;

CREATE TABLE tasks
(
    id            BIGINT NOT NULL,
    title         VARCHAR(255),
    description   VARCHAR(255),
    file          VARCHAR(255),
    image         VARCHAR(255),
    code          VARCHAR(255),
    deadline      TIMESTAMP WITHOUT TIME ZONE,
    created_at    date,
    updated_at    date,
    instructor_id BIGINT,
    lesson_id     BIGINT,
    trash_id      BIGINT,
    CONSTRAINT pk_tasks PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS test_seq START WITH 21 INCREMENT BY 1;

CREATE TABLE tests
(
    id            BIGINT  NOT NULL,
    title         VARCHAR(255),
    is_active     BOOLEAN,
    creation_date date,
    hour          INTEGER NOT NULL,
    minute        INTEGER NOT NULL,
    lesson_id     BIGINT,
    trash_id      BIGINT,
    CONSTRAINT pk_tests PRIMARY KEY (id)
);


CREATE SEQUENCE IF NOT EXISTS trash_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE trash
(
    id             BIGINT NOT NULL,
    name           VARCHAR(255),
    type           VARCHAR(255),
    date_of_delete TIMESTAMP WITHOUT TIME ZONE,
    cleaner_id     BIGINT,
    CONSTRAINT pk_trash PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS user_seq START WITH 41 INCREMENT BY 1;

CREATE TABLE users
(
    id           BIGINT NOT NULL,
    full_name    VARCHAR(255),
    email        VARCHAR(255),
    password     VARCHAR(255),
    role         VARCHAR(255),
    phone_number VARCHAR(255),
    block        BOOLEAN,
    uuid         VARCHAR(255),
    removed_date date,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS video_seq START WITH 21 INCREMENT BY 1;

CREATE TABLE videos
(
    id          BIGINT NOT NULL,
    description VARCHAR(255),
    created_at  date,
    updated_at  date,
    trash_id    BIGINT,
    lesson_id   BIGINT,
    CONSTRAINT pk_videos PRIMARY KEY (id)
);

