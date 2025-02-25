package today.todaysentence.domain.member;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemberUpdateAt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    @MapsId
    private Member member;

    @Column(name = "nickname_updated_at")
    private LocalDateTime nicknameUpdatedAt;

    @Column(name = "email_updated_at")
    private LocalDateTime emailUpdatedAt;

    @Column(name = "message_updated_at")
    private LocalDateTime messageUpdatedAt;

    private LocalDateTime deletedAt;


    public MemberUpdateAt(Member member){
        this.member = member;
        this.nicknameUpdatedAt = LocalDateTime.now().minusDays(1);
        this.emailUpdatedAt = LocalDateTime.now().minusDays(1);
        this.messageUpdatedAt = LocalDateTime.now().minusDays(1);
    }
    public void updateMessageTime(){this.messageUpdatedAt = LocalDateTime.now();}
    public void updateNicknameTime(){this.nicknameUpdatedAt = LocalDateTime.now();}
    public void updateEmailTime(){this.emailUpdatedAt = LocalDateTime.now();}



}
