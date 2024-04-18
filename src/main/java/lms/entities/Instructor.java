package lms.entities;

import jakarta.persistence.*;
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
    @SequenceGenerator(name = "instructor_gen",sequenceName = "instructor_seq", allocationSize = 1)
    private Long id;
    private String specialization;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    //********************************* User *************************************
    @OneToOne(cascade = CascadeType.REMOVE,orphanRemoval = true)
    private User user;

    //********************************* Course *************************************
    @ManyToMany(cascade = CascadeType.DETACH,fetch = FetchType.EAGER)
    private List<Course> courses;

    //********************************* Notification *******************************
    @OneToMany(mappedBy = "instructor", cascade = CascadeType.REMOVE,orphanRemoval = true)
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
