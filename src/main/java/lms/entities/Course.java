package lms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_gen")
    @SequenceGenerator(name = "course_seq",sequenceName = "course_seq", allocationSize = 1)
    private Long id;
    private String title;
    private String description;
    private String image;
    private LocalDate dateOfStart;
    private LocalDate dateOfEnd;

    //*************************************** Instructor *************************************
    @OneToMany(mappedBy = "course",cascade = CascadeType.DETACH,fetch = FetchType.EAGER)
    private List<Instructor> instructors = new ArrayList<>();

    //*************************************** Group ******************************************
    @ManyToOne(cascade = CascadeType.DETACH,fetch = FetchType.EAGER)
    private Group group;

    //*************************************** Lesson ******************************************
    @OneToMany(mappedBy = "course",cascade = CascadeType.REMOVE,fetch = FetchType.EAGER,orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dateOfStart = LocalDate.now();
    }
}
