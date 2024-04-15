package lms.entities;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
@Table(name = "tests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test_gen")
    @SequenceGenerator(name = "test_seq",sequenceName = "test_seq", allocationSize = 1)
    private Long id;
    private String title;
    private Boolean isActive;
    private LocalDate creationDate;
    private LocalDate updateDate;

    //*************************************** ResultTest **************************************
    @OneToMany(cascade = CascadeType.REMOVE,mappedBy = "test",orphanRemoval = true)
    private List<ResultTest> resultTests = new ArrayList<>();

    //*************************************** Question ****************************************
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "test",orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    //*************************************** Lesson ******************************************
    @OneToOne(cascade = CascadeType.DETACH)
    private Lesson lesson;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDate.now();
    }
}
