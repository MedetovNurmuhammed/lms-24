package lms.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_gen")
    @SequenceGenerator(name = "token_gen",sequenceName = "token_seq", allocationSize = 1)
    private Long id;
    @Column(unique = true)
    private String value;
    private LocalDateTime localDateTime;


    //*************************************** user **************************************
    @OneToOne()
    private User user;

    public Token( String value, LocalDateTime localDateTime, User user) {
        this.value = value;
        this.localDateTime = localDateTime;
        this.user = user;
    }
    public boolean isExpired(){
        return localDateTime.isBefore(LocalDateTime.now());
    }
}
