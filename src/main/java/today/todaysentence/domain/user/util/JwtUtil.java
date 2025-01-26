package today.todaysentence.domain.user.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.security.Key;

import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final Key secretKey;

    public JwtUtil() {
        // HS256 알고리즘에 적합한 크기의 키 생성
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 256비트 키 생성
    }

    // 토큰 생성
    public String generateToken(String email) {
        long expirationTime = 1000 * 60 * 60; // 1시간 유효기간 설정

        return Jwts.builder()
                .setSubject(email) // 사용자 이메일
                .setIssuedAt(new Date()) // 생성 시간
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 만료 시간
                .signWith(secretKey) // 안전한 비밀 키로 서명
                .compact();
    }

    // 토큰 검증
    public String validateToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey) // 안전한 비밀 키로 검증
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject(); // 이메일 반환
    }
}
