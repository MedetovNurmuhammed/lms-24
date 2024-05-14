package lms.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_gen")
    @SequenceGenerator(name = "comment_gen", sequenceName = "comment_seq", allocationSize = 1, initialValue = 21)
    private long id;
    private String content;
    private LocalDateTime createdAt;

    @OneToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private AnswerTask answerTask;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}