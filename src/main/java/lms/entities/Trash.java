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

    @OneToOne(mappedBy = "trash", cascade = {CascadeType.REMOVE}, optional = false, fetch =  FetchType.LAZY)
    private Student student;

    @OneToOne(mappedBy = "trash", cascade = CascadeType.REMOVE, optional = false, fetch =  FetchType.LAZY)
    private Group group;

    @OneToOne(mappedBy = "trash", cascade = CascadeType.REMOVE, optional = false, fetch =  FetchType.LAZY)
    private Course course;

    @OneToOne(mappedBy = "trash", cascade = CascadeType.REMOVE, optional = false, fetch =  FetchType.LAZY)
    private Instructor instructor;

    @OneToOne(mappedBy = "trash", cascade = CascadeType.REMOVE, optional = false, fetch =  FetchType.LAZY)
    private Video video;

    @OneToOne(mappedBy = "trash", cascade = CascadeType.REMOVE, optional = false, fetch =  FetchType.LAZY)
    private Link link;

    @OneToOne(mappedBy = "trash", cascade = CascadeType.REMOVE, optional = false, fetch =  FetchType.LAZY)
    private Task task;

    @OneToOne(mappedBy = "trash",cascade = CascadeType.REMOVE, optional = false, fetch =  FetchType.LAZY)
    private Presentation presentation;

    @OneToOne(mappedBy = "trash",cascade = CascadeType.REMOVE, optional = false, fetch =  FetchType.LAZY)
    private Test test;

    @OneToOne(mappedBy = "trash",cascade = CascadeType.REMOVE, optional = false, fetch =  FetchType.LAZY)
    private Lesson lesson;
}
