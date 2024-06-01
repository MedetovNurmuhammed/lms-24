CREATE SEQUENCE exam_seq ;
CREATE SEQUENCE exam_result_seq;

create table exams(
                      id  BIGINT PRIMARY KEY DEFAULT nextval('exam_seq'),
                      title varchar(255),
                      exam_date DATE,
                      update_date DATE);
create table exam_results(
                             id  BIGINT PRIMARY KEY DEFAULT nextval('exam_result_seq'),
                             point int,
                             exam_id BIGINT,
                             student_id BIGINT,
                             FOREIGN KEY (exam_id) REFERENCES exams(id),
                             FOREIGN KEY (student_id) REFERENCES students(id);

)