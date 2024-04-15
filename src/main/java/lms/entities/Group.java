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
@Table(name = "groups")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_gen")
    @SequenceGenerator(name = "group_seq",sequenceName = "group_seq", allocationSize = 1)
    private Long id;
    private String title;
    private String description;
    private String image;
    private LocalDate dateOfStart;
    private LocalDate dateOfEnd;

    //*************************************** Course ******************************************
    @ManyToMany(mappedBy = "groups",cascade = CascadeType.REMOVE)
    private List<Course> courses = new ArrayList<>();

    //*************************************** Student ******************************************
    @OneToMany(mappedBy = "group",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Student> students = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dateOfStart = LocalDate.now();
    }
}
