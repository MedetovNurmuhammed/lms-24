package lms.entities;

import jakarta.persistence.*;
import lms.enums.Type;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trash")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Trash {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trash_gen")
    @SequenceGenerator(name = "trash_gen", sequenceName = "trash_seq", allocationSize = 1)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;
    private ZonedDateTime dateOfDelete;

    @OneToOne(mappedBy = "trash", cascade = {CascadeType.REMOVE},optional = false)
    private Student student;

    @OneToOne(mappedBy = "trash", cascade = CascadeType.REMOVE,optional = false)
    private Group group;

    @OneToOne(mappedBy = "trash", cascade = CascadeType.REMOVE,optional = false)
    private Course course;
}
