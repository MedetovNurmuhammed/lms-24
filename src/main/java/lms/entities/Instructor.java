package lms.entities;

import jakarta.persistence.*;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import lms.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @SequenceGenerator(name = "instructor_gen", sequenceName = "instructor_seq", allocationSize = 1, initialValue = 21)
    private Long id;
    private String specialization;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    //********************************* User *************************************
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true, fetch =  FetchType.LAZY)
    private User user;

    //********************************* Course *************************************
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private List<Course> courses = new ArrayList<>();

    //********************************* Notification *******************************
    @ElementCollection(fetch =  FetchType.LAZY)
    private Map<Notification, Boolean> notificationStates = new HashMap<>();

    //********************************* Trash ***************************************
    @OneToOne(fetch =  FetchType.LAZY)
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
