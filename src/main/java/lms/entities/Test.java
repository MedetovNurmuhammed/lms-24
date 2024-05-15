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
@Table(name = "tests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test_gen")
    @SequenceGenerator(name = "test_gen", sequenceName = "test_seq", allocationSize = 1, initialValue = 21)
    private Long id;
    private String title;
    private Boolean isActive;
    private LocalDate creationDate;
    private int hour;
    private int minute;

    //*************************************** ResultTest **************************************
    @OneToMany(mappedBy = "test", orphanRemoval = true)
    private List<ResultTest> resultTests = new ArrayList<>();

    //*************************************** Question ****************************************
    @OneToMany(mappedBy = "test",cascade = {CascadeType.PERSIST, CascadeType.MERGE},orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    //*************************************** Lesson ******************************************
    @ManyToOne(cascade = CascadeType.DETACH)
    private Lesson lesson;

    @OneToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private Trash trash;
    @PrePersist
    protected void onCreate() {
        creationDate = LocalDate.now();
    }
}
