package today.todaysentence.global.jwt;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import today.todaysentence.domain.member.repository.MemberRepository;
import today.todaysentence.global.exception.exception.BaseException;
import today.todaysentence.global.redis.RedisService;
import today.todaysentence.global.security.userDetails.CustomUserDetails;
import today.todaysentence.global.security.userDetails.JwtUserDetails;
import today.todaysentence.global.security.userDetails.UserDetailsServiceImpl;

import java.security.Key;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.*;

import static today.todaysentence.global.exception.exception.ExceptionCode.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    private static final String BEARER_PREFIX ="Bearer ";
    public static final String ACCESS_KEY ="ACCESS-TOKEN";
    public static final String REFRESH_KEY ="REFRESH-TOKEN";
    private static final long ACCESS_TIME = Duration.ofMinutes(30).toMillis();
    private static final long REFRESH_TIME = Duration.ofDays(7).toMillis();



    @Value("${jwt.secret.key}")
    private String secretKey;  

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
        Object principal = authentication.getPrincipal();

        Long userId = getUserId(principal);
        String nickname = getNickname(principal);

        if(type.equals("Access")) {
            return BEARER_PREFIX
                    + Jwts.builder()
                    .setSubject(authentication.getName())
                    .claim("nickname", nickname)
                    .claim("id",userId)
                    .signWith(key)
                    .setIssuedAt(date)
                    .setExpiration(new Date(date.getTime() + ACCESS_TIME))
                    .compact();
        } else {
            return BEARER_PREFIX
                    + Jwts.builder()
                    .setSubject(authentication.getName())
                    .claim("id",userId)
                    .claim("nickname", nickname)
                    .signWith(key)
                    .setIssuedAt(date)
                    .setExpiration(new Date(date.getTime() + REFRESH_TIME))
                    .compact();
        }
    }

    private Long getUserId(Object principal) {
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).member().getId();
        } else if (principal instanceof JwtUserDetails) {
            return ((JwtUserDetails) principal).id();
        }
        return null;
    }

    private String getNickname(Object principal) {
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getMemberNickname();
        } else if (principal instanceof JwtUserDetails) {
            return ((JwtUserDetails) principal).nickname();
        }
        return null;
    }

    public boolean validateToken(String token) {
        if (redisService.isBlacklisted(token)) {
            return false;
        }

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않은 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
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
        Claims claims = parseClaims(accessToken);

        String username = claims.getSubject();
        String nickname = claims.get("nickname", String.class);
        Long id = claims.get("id", Long.class);

        return new UsernamePasswordAuthenticationToken(new JwtUserDetails(id,username,nickname), null, Collections.emptyList());
    }
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
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
        Claims claims= parseClaims(refreshToken);
        String memberEmail = claims.getSubject();
        Long id = claims.get("id", Long.class);
        String nickname = claims.get("nickname", String.class);
        String deviceId = request.getHeader("Device-Id");

        validateDeviceId(memberEmail, deviceId);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(new JwtUserDetails(id,memberEmail,nickname), null,  Collections.emptyList());

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
