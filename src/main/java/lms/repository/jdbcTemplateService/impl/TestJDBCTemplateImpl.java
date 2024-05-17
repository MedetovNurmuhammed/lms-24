package lms.repository.jdbcTemplateService.impl;

import lms.dto.response.StudentTestResponse;
import lms.repository.jdbcTemplateService.TestJDBCTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TestJDBCTemplateImpl implements TestJDBCTemplate {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<StudentTestResponse> allStudentsWithResultTest(Long testId) {
        return jdbcTemplate.query("""
                        select
                            rt.id as result_test_id,
                            u.full_name,
                            rt.point
                        from
                            tests t
                            join lessons l on t.lesson_id = l.id
                            join courses c on l.course_id = c.id
                            join courses_groups cg on c.id = cg.courses_id
                            join groups g on cg.groups_id = g.id
                            join students s on g.id = s.group_id
                            join users u on s.user_id = u.id
                            left outer join result_tests rt on t.id = rt.test_id and s.id = rt.student_id
                        where
                            t.id = ?
                        """,
                new Object[]{testId},
                (rs, rowNum) -> {
                    Long resultTestId = rs.getLong("result_test_id");
                    String fullName = rs.getString("full_name");
                    Integer point = rs.getObject("point", Integer.class);
                    boolean isPassed = point != null;
                    return new StudentTestResponse(resultTestId, fullName, point != null ? point : 0, isPassed);
                });
    }
}
