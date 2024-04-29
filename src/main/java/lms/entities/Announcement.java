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
@Table(name = "announcements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "announcement_gen")
    @SequenceGenerator(name = "announcement_gen",sequenceName = "announcement_seq", allocationSize = 1)
    private long id;
    private String announcementContent;
    private Boolean isPublished;
    private LocalDate publishedDate;
    private LocalDate expirationDate;

    //******************************* User **********************************************
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private User user;

    //******************************* Group **********************************************
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private List<Group> groups = new ArrayList<>();

}
