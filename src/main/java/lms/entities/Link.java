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
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private Lesson lesson;

    //***************************************** Task *********************************
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private Task task;

    //********************************* Trash ***************************************
    @OneToOne
    private Trash trash;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private Video video;

}
