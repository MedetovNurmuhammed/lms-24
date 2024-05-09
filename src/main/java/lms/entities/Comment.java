package lms.entities;

import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_gen")
    @SequenceGenerator(name = "comment_gen", sequenceName = "comment_seq", allocationSize = 1, initialValue = 21)
    private long id;
    private String content;

    @OneToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private Instructor instructor;
    @OneToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private Student student;
    @ManyToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private AnswerTask answerTask;
}
