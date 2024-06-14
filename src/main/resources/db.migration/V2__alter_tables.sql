ALTER TABLE announcements
    ADD CONSTRAINT FK_ANNOUNCEMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE announcements_groups
    ADD CONSTRAINT fk_anngro_on_announcement FOREIGN KEY (courses_id) REFERENCES announcements (id);

ALTER TABLE announcements_groups
    ADD CONSTRAINT fk_anngro_on_group FOREIGN KEY (groups_id) REFERENCES groups (id);

ALTER TABLE answer_tasks
    ADD CONSTRAINT FK_ANSWER_TASKS_ON_LINK FOREIGN KEY (link_id) REFERENCES links (id);

ALTER TABLE answer_tasks
    ADD CONSTRAINT FK_ANSWER_TASKS_ON_STUDENT FOREIGN KEY (student_id) REFERENCES students (id);

ALTER TABLE answer_tasks
    ADD CONSTRAINT FK_ANSWER_TASKS_ON_TASK FOREIGN KEY (task_id) REFERENCES tasks (id);

CREATE SEQUENCE IF NOT EXISTS comment_seq START WITH 21 INCREMENT BY 1;


ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_ANSWER_TASK FOREIGN KEY (answer_task_id) REFERENCES answer_tasks (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE courses
    ADD CONSTRAINT FK_COURSES_ON_TRASH FOREIGN KEY (trash_id) REFERENCES trash (id);

ALTER TABLE courses_groups
    ADD CONSTRAINT fk_cougro_on_course FOREIGN KEY (courses_id) REFERENCES courses (id);

ALTER TABLE courses_groups
    ADD CONSTRAINT fk_cougro_on_group FOREIGN KEY (groups_id) REFERENCES groups (id);

ALTER TABLE exams
    ADD CONSTRAINT FK_EXAMS_ON_COURSE FOREIGN KEY (course_id) REFERENCES courses (id);

ALTER TABLE exam_results
    ADD CONSTRAINT FK_EXAM_RESULTS_ON_EXAM FOREIGN KEY (exam_id) REFERENCES exams (id);

ALTER TABLE exam_results
    ADD CONSTRAINT FK_EXAM_RESULTS_ON_STUDENT FOREIGN KEY (student_id) REFERENCES students (id);

ALTER TABLE groups
    ADD CONSTRAINT FK_GROUPS_ON_TRASH FOREIGN KEY (trash_id) REFERENCES trash (id);

ALTER TABLE instructors
    ADD CONSTRAINT FK_INSTRUCTORS_ON_TRASH FOREIGN KEY (trash_id) REFERENCES trash (id);

ALTER TABLE instructors
    ADD CONSTRAINT FK_INSTRUCTORS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE instructors_courses
    ADD CONSTRAINT fk_inscou_on_course FOREIGN KEY (courses_id) REFERENCES courses (id);

ALTER TABLE instructors_courses
    ADD CONSTRAINT fk_inscou_on_instructor FOREIGN KEY (instructors_id) REFERENCES instructors (id);

ALTER TABLE lessons_presentations
    ADD CONSTRAINT uc_lessons_presentations_presentations UNIQUE (presentations_id);

ALTER TABLE lessons_videos
    ADD CONSTRAINT uc_lessons_videos_videos UNIQUE (videos_id);

ALTER TABLE lessons
    ADD CONSTRAINT FK_LESSONS_ON_COURSE FOREIGN KEY (course_id) REFERENCES courses (id);

ALTER TABLE lessons
    ADD CONSTRAINT FK_LESSONS_ON_TRASH FOREIGN KEY (trash_id) REFERENCES trash (id);

ALTER TABLE lessons_presentations
    ADD CONSTRAINT fk_lespre_on_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id);

ALTER TABLE lessons_presentations
    ADD CONSTRAINT fk_lespre_on_presentation FOREIGN KEY (presentations_id) REFERENCES presentations (id);

ALTER TABLE lessons_videos
    ADD CONSTRAINT fk_lesvid_on_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id);

ALTER TABLE lessons_videos
    ADD CONSTRAINT fk_lesvid_on_video FOREIGN KEY (videos_id) REFERENCES videos (id);

ALTER TABLE links
    ADD CONSTRAINT FK_LINKS_ON_LESSON FOREIGN KEY (lesson_id) REFERENCES lessons (id);

ALTER TABLE links
    ADD CONSTRAINT FK_LINKS_ON_TASK FOREIGN KEY (task_id) REFERENCES tasks (id);

ALTER TABLE links
    ADD CONSTRAINT FK_LINKS_ON_TRASH FOREIGN KEY (trash_id) REFERENCES trash (id);

ALTER TABLE links
    ADD CONSTRAINT FK_LINKS_ON_VIDEO FOREIGN KEY (video_id) REFERENCES videos (id);


ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_ANSWER_TASK FOREIGN KEY (answer_task_id) REFERENCES answer_tasks (id);

ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_TASK FOREIGN KEY (task_id) REFERENCES tasks (id);

ALTER TABLE options
    ADD CONSTRAINT FK_OPTIONS_ON_QUESTION FOREIGN KEY (question_id) REFERENCES questions (id);

ALTER TABLE presentations
    ADD CONSTRAINT FK_PRESENTATIONS_ON_LESSON FOREIGN KEY (lesson_id) REFERENCES lessons (id);

ALTER TABLE presentations
    ADD CONSTRAINT FK_PRESENTATIONS_ON_TRASH FOREIGN KEY (trash_id) REFERENCES trash (id);

ALTER TABLE questions
    ADD CONSTRAINT FK_QUESTIONS_ON_TEST FOREIGN KEY (test_id) REFERENCES tests (id);
ALTER TABLE result_tests
    ADD CONSTRAINT FK_RESULT_TESTS_ON_STUDENT FOREIGN KEY (student_id) REFERENCES students (id);

ALTER TABLE result_tests
    ADD CONSTRAINT FK_RESULT_TESTS_ON_TEST FOREIGN KEY (test_id) REFERENCES tests (id);

ALTER TABLE result_tests_options
    ADD CONSTRAINT fk_restesopt_on_option FOREIGN KEY (options_id) REFERENCES options (id);

ALTER TABLE result_tests_options
    ADD CONSTRAINT fk_restesopt_on_result_test FOREIGN KEY (result_test_id) REFERENCES result_tests (id);

ALTER TABLE students
    ADD CONSTRAINT FK_STUDENTS_ON_GROUP FOREIGN KEY (group_id) REFERENCES groups (id);

ALTER TABLE students
    ADD CONSTRAINT FK_STUDENTS_ON_TRASH FOREIGN KEY (trash_id) REFERENCES trash (id);

ALTER TABLE students
    ADD CONSTRAINT FK_STUDENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE tasks
    ADD CONSTRAINT FK_TASKS_ON_INSTRUCTOR FOREIGN KEY (instructor_id) REFERENCES instructors (id);

ALTER TABLE tasks
    ADD CONSTRAINT FK_TASKS_ON_LESSON FOREIGN KEY (lesson_id) REFERENCES lessons (id);

ALTER TABLE tasks
    ADD CONSTRAINT FK_TASKS_ON_TRASH FOREIGN KEY (trash_id) REFERENCES trash (id);

ALTER TABLE tests
    ADD CONSTRAINT FK_TESTS_ON_LESSON FOREIGN KEY (lesson_id) REFERENCES lessons (id);

ALTER TABLE tests
    ADD CONSTRAINT FK_TESTS_ON_TRASH FOREIGN KEY (trash_id) REFERENCES trash (id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE videos
    ADD CONSTRAINT FK_VIDEOS_ON_LESSON FOREIGN KEY (lesson_id) REFERENCES lessons (id);

ALTER TABLE videos
    ADD CONSTRAINT FK_VIDEOS_ON_TRASH FOREIGN KEY (trash_id) REFERENCES trash (id);
