package lms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "announcements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "announcement_gen")
    @SequenceGenerator(name = "announcement_gen",sequenceName = "announcement_seq", allocationSize = 1, initialValue = 21)
    private long id;
    private String announcementContent;
    private Boolean isPublished;
    private LocalDate publishedDate;
    private LocalDate expirationDate;

    //******************************* User **********************************************
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private User user;

    //******************************* Group **********************************************
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    private List<Group> groups = new ArrayList<>();

}
