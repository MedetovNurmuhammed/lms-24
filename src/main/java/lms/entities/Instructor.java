package lms.entities;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instructors")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "instructor_gen")
    @SequenceGenerator(name = "instructor_seq",sequenceName = "instructor_seq", allocationSize = 1)
    private Long id;
    private String specialization;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    //********************************* User *************************************
    @OneToOne(cascade = CascadeType.REMOVE)
    private User user;

    //********************************* Course *************************************
    @ManyToOne(cascade = CascadeType.DETACH)
    private Course course;

    //********************************* Notification *******************************
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Notification> notifications = new ArrayList<>();

   @PrePersist
    protected void onCreate() {
       this.createdAt = LocalDate.now();
   }

   @PreUpdate
    protected void onUpdate() {
       this.updatedAt = LocalDate.now();
   }
}
