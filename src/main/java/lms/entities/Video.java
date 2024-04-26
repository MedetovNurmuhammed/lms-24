package lms.entities;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.CascadeType;
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

    //*************************************** Link ***************************************
    @OneToOne(mappedBy = "video", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Link link;
    //********************************* Trash ***************************************
    @OneToOne
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
