package lms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
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
    @SequenceGenerator(name = "group_gen",sequenceName = "group_seq", allocationSize = 1,initialValue = 21)
    private Long id;
    private String title;
    private String description;
    private String image;
    private LocalDate dateOfStart;
    private LocalDate dateOfEnd;
    private LocalDate removedDate;

    //*************************************** Course ******************************************
    @ManyToMany(mappedBy = "groups",cascade = CascadeType.DETACH)
    private List<Course> courses = new ArrayList<>();

    //*************************************** Student ******************************************
    @OneToMany(mappedBy = "group",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Student> students = new ArrayList<>();

    //*************************************** Trash ********************************************
    @OneToOne
    private Trash trash;
}
