package today.todaysentence.domain.member;

import jakarta.persistence.*;
import lombok.*;
import today.todaysentence.global.timeStamped.Timestamped;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "id")
public class Member extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    @Setter
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false,unique = true)
    @Setter
    private String nickname;

    @Setter
    @Builder.Default
    private String statusMessage = "상태메시지를 입력해주세요.";

    @Setter
    @Builder.Default
    private String profileImg = "basicProfileUrl";

    @Column
    @Builder.Default
    private Boolean isDeleted = false;


    public void withdraw(){
        this.isDeleted =true;
        this.email +="_W_"+ UUID.randomUUID().toString();
        this.nickname+="_W_"+UUID.randomUUID().toString();
        this.setDeletedAt(LocalDateTime.now());
    }

}
