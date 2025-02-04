package today.todaysentence.global.jwt;

import lombok.Getter;

public record TokenDto(String accessToken,String refreshToken) {

}