package today.todaysentence.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.member.repository.MemberRepository;
import today.todaysentence.global.exception.exception.BaseException;
import today.todaysentence.global.redis.RedisService;
import today.todaysentence.global.security.userDetails.CustomUserDetails;
import today.todaysentence.global.security.userDetails.UserDetailsServiceImpl;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.*;

import static today.todaysentence.global.exception.exception.ExceptionCode.*;


@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final String BEARER_PREFIX ="Bearer ";
    public static final String ACCESS_KEY ="ACCESS-TOKEN";
    public static final String REFRESH_KEY ="REFRESH-TOKEN";
    private static final long ACCESS_TIME = Duration.ofMinutes(5).toMillis();
    private static final long REFRESH_TIME = Duration.ofDays(7).toMillis();
    private final MemberRepository memberRepository;



    @Value("${jwt.secret.key}")
    private String secretKey;  // Base64로 인코딩된 값이므로, String으로 받을 수 있습니다.

    private Key key;

    private final UserDetailsServiceImpl userDetailsService;
    private final RedisService redisService;

    @PostConstruct
    public void init() {
        this.key = getKeyFromBase64EncodedKey(this.secretKey);
    }

    private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        byte[] keyBytes = Base64.getDecoder().decode(base64EncodedSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String resolveToken(HttpServletRequest request, String token) {
        String tokenName = token.equals("ACCESS-TOKEN") ? ACCESS_KEY : REFRESH_KEY;
        String bearerToken = request.getHeader(tokenName);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public TokenDto createAllToken(Authentication authentication) {
        return new TokenDto(createToken(authentication, "Access"),
                createToken(authentication, "Refresh"));
    }

    public String createToken(Authentication authentication, String type) {
        Date date = new Date();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        if(type.equals("Access")) {
            return BEARER_PREFIX
                    + Jwts.builder()
                    .setSubject(authentication.getName())
                    .claim("nickname", customUserDetails.getMemberNickname())
                    .signWith(key)
                    .setIssuedAt(date)
                    .setExpiration(new Date(date.getTime() + ACCESS_TIME))
                    .compact();
        } else {
            return BEARER_PREFIX
                    + Jwts.builder()
                    .setSubject(authentication.getName())
                    .signWith(key)
                    .setIssuedAt(date)
                    .setExpiration(new Date(date.getTime() + REFRESH_TIME))
                    .compact();
        }
    }

    public boolean validateToken(String token) {
        if (redisService.isBlacklisted(token)) {
            return false;
        }

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException | SecurityException | MalformedJwtException e) {
            throw new BaseException(INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new BaseException(EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new BaseException(UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new BaseException(WRONG_TOKEN);
        }
    }

    public void setHeaderToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setHeader(ACCESS_KEY, accessToken);
        response.setHeader(REFRESH_KEY, refreshToken);
    }

    public void setAuthentication(String accessToken) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = getAuthentication(accessToken);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }


    public Authentication getAuthentication(String accessToken) {
        String memberEmail = parseClaims(accessToken);

//추후 권한이 확장된다면 추가
//        String role = claims.get("role", String.class);
//        if (role == null) {
//            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
//        }
//
//        Collection<? extends GrantedAuthority> authorities =
//                Arrays.stream(role.split(","))
//                        .map(SimpleGrantedAuthority::new)
//                        .collect(Collectors.toList());
        UserDetails userDetails = userDetailsService.loadUserByUsername(memberEmail);

        return new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
    }
    public String parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public void createTokenAndSaved(Authentication authentication, HttpServletResponse response,HttpServletRequest request, String memberEmail) {
        TokenDto token = createAllToken(authentication);
        String deviceId = request.getHeader("Device-Id");
        String accessToken = token.accessToken();
        String refreshToken = token.refreshToken();
        setHeaderToken(response,accessToken,refreshToken);

        redisService.saveRefreshToken(memberEmail,refreshToken,deviceId,REFRESH_TIME);
    }

    public void handleRefreshToken(String refreshToken, HttpServletResponse response, HttpServletRequest request) {
        String memberEmail = parseClaims(refreshToken);
        String deviceId = request.getHeader("Device-Id");

        validateDeviceId(memberEmail, deviceId);

        UserDetails userDetails = userDetailsService.loadUserByUsername(memberEmail);


        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        TokenDto tokenDto = createAllToken(authentication);

        setHeaderToken(response, tokenDto.accessToken(),tokenDto.refreshToken());

        redisService.saveRefreshToken(memberEmail, tokenDto.refreshToken(), deviceId, REFRESH_TIME);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

    }
    private void validateDeviceId(String memberEmail, String deviceId) {
        MemberDeviceIdDto memberDeviceIdDto = redisService.getRefreshToken(memberEmail);
        if (memberDeviceIdDto == null || deviceId == null || !deviceId.equals(memberDeviceIdDto.deviceId())) {
            redisService.deleteRefreshToken(memberEmail);
            throw new BaseException(NOT_ACCORDANCE_DEVICE_ID);
        }
    }

    public long getExpirationTime(String token) {
        Date expirationDate = Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getExpiration();
        Date now = new Date();
        return expirationDate.getTime() - now.getTime();
    }
}
