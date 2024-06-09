package lms.entities;

import jakarta.persistence.*;
import lms.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import static jakarta.persistence.CascadeType.MERGE;

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
    @SequenceGenerator(name = "question_gen",sequenceName = "question_seq", allocationSize = 1,initialValue = 21)
    private Long id;
    private String title;
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;
    private double point;

    //*************************************** Option ***************************************
    @OneToMany(cascade = {CascadeType.PERSIST,MERGE}, mappedBy = "question",orphanRemoval = true, fetch =  FetchType.LAZY)
    private List<Option> options = new ArrayList<>();

    //*************************************** Test ******************************************
    @ManyToOne(cascade = CascadeType.DETACH, fetch =  FetchType.LAZY)
    private Test test;

}
