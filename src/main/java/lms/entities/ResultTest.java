package lms.entities;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
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
@Table(name = "result_tests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultTest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "res_test_gen")
    @SequenceGenerator(name = "res_test_seq", sequenceName = "res_test_seq", allocationSize = 1)
    private Long id;
    private int point;
    private LocalDate creationDate;
    private LocalDate updateDate;

    //*************************************** Test ********************************************
    @ManyToOne(cascade = CascadeType.DETACH)
    private Test test;

    //*************************************** Options ******************************************
    @ManyToMany(cascade = CascadeType.DETACH)
    private List<Option> options = new ArrayList<>();

    //*************************************** Student ******************************************
    @ManyToOne(cascade = CascadeType.DETACH)
    private Student student;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDate.now();
    }
}
