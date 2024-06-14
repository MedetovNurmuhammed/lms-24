package lms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.FetchType;
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
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true, fetch =  FetchType.LAZY)
    private List<ResultTest> resultTests = new ArrayList<>();

    //*************************************** Question ****************************************
    @OneToMany(mappedBy = "test",cascade = {CascadeType.ALL},orphanRemoval = true, fetch =  FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();

    //*************************************** Lesson ******************************************
    @ManyToOne(cascade = CascadeType.DETACH, fetch =  FetchType.LAZY)
    private Lesson lesson;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Trash trash;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDate.now();
    }
}
