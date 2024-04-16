package lms.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
    @SequenceGenerator(name = "token_gen",sequenceName = "token_seq", allocationSize = 1,initialValue = 41)
    private Long id;
    private String uuiValue;
    @OneToOne(cascade = {CascadeType.DETACH})
    private User user;
}
