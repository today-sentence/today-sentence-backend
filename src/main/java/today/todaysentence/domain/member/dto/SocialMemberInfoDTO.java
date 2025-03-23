package today.todaysentence.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SocialMemberInfoDTO {
    private String username;
    private String nickname;
    private String email;
    private String profileImage;
}