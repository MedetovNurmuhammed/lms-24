package lms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
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

    @OneToOne(mappedBy = "trash", cascade = {CascadeType.REMOVE}, optional = false)
    private Student student;

    @OneToOne(mappedBy = "trash", cascade = CascadeType.REMOVE, optional = false)
    private Group group;

    @OneToOne(mappedBy = "trash", cascade = CascadeType.REMOVE, optional = false)
    private Course course;

    @OneToOne(mappedBy = "trash", cascade = CascadeType.REMOVE, optional = false)
    private Instructor instructor;

    @OneToOne(mappedBy = "trash", cascade = CascadeType.REMOVE, optional = false)
    private Video video;

    @OneToOne(mappedBy = "trash", cascade = CascadeType.REMOVE, optional = false)
    private Link link;

    @OneToOne(mappedBy = "trash", cascade = CascadeType.REMOVE, optional = false)
    private Task task;

    @OneToOne(mappedBy = "trash",cascade = CascadeType.REMOVE, optional = false)
    private Presentation presentation;
}
