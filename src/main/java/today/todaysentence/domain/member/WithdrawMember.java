package today.todaysentence.domain.member;

import jakarta.persistence.*;
import lombok.*;
import today.todaysentence.global.timeStamped.Timestamped;


@Getter
@Table(name = "withdraw_member", indexes = @Index(name = "idx_email", columnList = "email"))
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawMember extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

}
