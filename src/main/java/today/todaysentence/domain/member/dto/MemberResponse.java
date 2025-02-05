package today.todaysentence.domain.member.dto;

import today.todaysentence.domain.member.Member;

import java.time.LocalDateTime;

public class MemberResponse {

    public record Success() {
    }

    public record MemberInfo(
            Long id,
            String email,
            String nickname
    ) {
        public MemberInfo(Member member) {
            this(member.getId(), member.getEmail(), member.getNickname());
        }
    }

    public record WithdrawResponse(
            String message,
            LocalDateTime time
    ) {

    }
}
