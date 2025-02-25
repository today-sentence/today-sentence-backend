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
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false,unique = true)
    private String nickname;

    @Builder.Default
    private String statusMessage = "상태메시지를 입력해주세요.";

    private Long todayPostId;

    @Setter
    @Builder.Default
    private String profileImg = "basicProfileUrl";

    @Column
    @Builder.Default
    private Boolean isDeleted = false;

    public void passwordChange(String newPassword){
        this.password = newPassword;
    }

    public void withdraw(){
        this.isDeleted =true;
        this.email +="_W_"+ UUID.randomUUID().toString();
        this.nickname+="_W_"+UUID.randomUUID().toString();
        this.deletedAt=LocalDateTime.now();
    }

    public void changeEmail(String email){
        this.email = email;
    }

    public void changeNickname(String nickname){
        this.nickname = nickname;
    }

    public void changeMessage(String message){
        this.statusMessage = message;
    }

    public void insertTodaySentenceId(Long id){ this.todayPostId = id;}

    public void removeTodaySentenceId(){ this.todayPostId = null;}


}
