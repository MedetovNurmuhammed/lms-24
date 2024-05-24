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
public class Comment extends BaseEntity {

    private String content;

    @OneToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private AnswerTask answerTask;

}
