package lms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.File;
import java.time.LocalDate;

@Entity
@Table(name = "presentations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Presentation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "presentation_gen")
    @SequenceGenerator(name = "presentation_gen", sequenceName = "presentation_seq", allocationSize = 1, initialValue = 21)
    private Long id;
    private String title;
    private String description;
    private String file;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Lesson lesson;

    @OneToOne()
    private Trash trash;


    @PrePersist
    private void prePersist() {
        createdAt = LocalDate.now();
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDate.now();
    }
}
