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
@Table(name = "videos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "video_gen")
    @SequenceGenerator(name = "video_gen", sequenceName = "video_seq", allocationSize = 1, initialValue = 21)
    private Long id;
    private String description;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    @Enumerated(EnumType.STRING)
    private Type type;

    //*************************************** Link ***************************************
    @OneToOne(mappedBy = "video", cascade = CascadeType.REMOVE,orphanRemoval = true)
    private Link link ;
    //*************************************** Lesson ***************************************
    @ManyToOne( cascade = CascadeType.DETACH)
    private Lesson lesson ;

    @OneToOne(fetch = FetchType.LAZY)
    private Trash trash;



    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }
}
