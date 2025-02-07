package today.todaysentence.domain.member.dto;

import today.todaysentence.domain.member.Member;

import java.time.LocalDateTime;

public class MemberResponse {

    public record Success() {
    }

    public record MemberInfo(
            String email,
            String nickname,
            String statusMessage,
            String profileImg
    ) {
        public MemberInfo(Member member) {
            this( member.getEmail(), member.getNickname() , member.getStatusMessage(), member.getProfileImg());
        }
    }

    public record ActionStatusResponse(
            String message,
            LocalDateTime time
    ) {
        public ActionStatusResponse(String message, LocalDateTime time) {
            this.message = message;
            this.time = time.withSecond(0).withNano(0);
        }

    }
}
