package lms.dto.response;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
public class DataResponses {
    private int groups;
    private int students;
    private int instructors;
    private int courses;
    private int graduated;
    private int year;

    public DataResponses(Long groups, Long students, Long instructors, Long courses, Long graduated, Integer year) {
        this.groups = groups.intValue();
        this.students = students.intValue();
        this.instructors = instructors.intValue();
        this.courses = courses.intValue();
        this.graduated = graduated.intValue();
        this.year = year;
    }
}
