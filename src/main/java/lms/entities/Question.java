package lms.entities;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lms.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_gen")
    @SequenceGenerator(name = "question_seq",sequenceName = "question_seq", allocationSize = 1)
    private Long id;
    private String title;
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;
    private int point;
    //*************************************** Option ***************************************
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "question",orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    //*************************************** Test ******************************************
    @ManyToOne(cascade = CascadeType.DETACH)
    private Test test;

}
