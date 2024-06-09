package lms.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
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
    @SequenceGenerator(name = "link_gen", sequenceName = "link_seq", allocationSize = 1, initialValue = 30)
    private Long id;
    private String title;
    private String url;

    //***************************************** AnswerTask *********************************
    @ManyToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private Lesson lesson;

    //********************************* Trash ***************************************
    @OneToOne(fetch =  FetchType.LAZY)
    private Trash trash;

    //********************************* Video ***************************************
    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private Video video;


}
