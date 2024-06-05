package lms.entities;

import jakarta.persistence.*;
import lms.enums.Type;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.time.ZonedDateTime;

@Entity
@Table(name = "trash")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trash {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trash_gen")
    @SequenceGenerator(name = "trash_gen", sequenceName = "trash_seq", allocationSize = 1)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;
    private ZonedDateTime dateOfDelete;


    @OneToOne(mappedBy = "trash")
    private Student student;

    @OneToOne(mappedBy = "trash")
    private Group group;

    @OneToOne(mappedBy = "trash")
    private Course course;

    @ManyToOne()
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    @OneToOne(mappedBy = "trash")
    private Lesson lesson;

    @OneToOne(mappedBy = "trash")
    private Link link;

    @OneToOne(mappedBy = "trash")
    private Video video;

    @OneToOne(mappedBy = "trash")
    private Task task;

    @OneToOne(mappedBy = "trash")
    private Presentation presentation;

    @OneToOne(mappedBy = "trash")
    private Test test;

    @OneToOne(mappedBy = "trash",cascade = CascadeType.REMOVE, optional = false)
    private Lesson lesson;
}
