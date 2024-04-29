package lms.entities;

import jakarta.persistence.*;
import lms.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "links")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "link_gen")
    @SequenceGenerator(name = "link_seq",sequenceName = "link_seq", allocationSize = 1)
    private Long id;
    private String title;
    private String url;
    @Enumerated(EnumType.STRING)
    private Type type;

    //***************************************** AnswerTask *********************************
    @ManyToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private Lesson lesson;

    //***************************************** Task *********************************
    @ManyToOne( cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private Task task;

    @OneToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private Video video;

    @OneToOne(mappedBy = "link",cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private AnswerTask answerTask;

    @OneToOne
    private Trash trash;
}
