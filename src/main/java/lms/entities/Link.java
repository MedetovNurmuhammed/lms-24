package lms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @SequenceGenerator(name = "link_gen", sequenceName = "link_seq", allocationSize = 1, initialValue = 21)
    private Long id;
    private String title;
    private String url;

    //***************************************** AnswerTask *********************************
    @ManyToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private Lesson lesson;

    //***************************************** Task *********************************
    @ManyToOne( cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private Task task;

    //********************************* Trash ***************************************
    @OneToOne(fetch = FetchType.LAZY)
    private Trash trash;

    @OneToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private Video video;

    @OneToOne(mappedBy = "link",cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private AnswerTask answerTask;

}
