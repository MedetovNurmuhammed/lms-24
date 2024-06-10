package lms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "options")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "option_gen")
    @SequenceGenerator(name = "option_gen", sequenceName = "option_seq", allocationSize = 1, initialValue = 21)
    private Long id;
    private String option;
    private Boolean isTrue;

    //*************************************** Question *************************************
    @ManyToOne(cascade = CascadeType.DETACH, fetch =  FetchType.LAZY)
    private Question question;
}
