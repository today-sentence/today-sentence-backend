package today.todaysentence.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import today.todaysentence.domain.member.repository.MemberRepository;
import today.todaysentence.global.redis.RedisService;

import java.io.IOException;

import static today.todaysentence.global.jwt.JwtUtil.ACCESS_KEY;
import static today.todaysentence.global.jwt.JwtUtil.REFRESH_KEY;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String access_token = jwtUtil.resolveToken(request, ACCESS_KEY);
        String refresh_token = jwtUtil.resolveToken(request, REFRESH_KEY);

        if(access_token != null && jwtUtil.validateToken(access_token)) {
            jwtUtil.setAuthentication(access_token);
        }else if (refresh_token != null && jwtUtil.validateToken(refresh_token)) {
            jwtUtil.handleRefreshToken(refresh_token, response,request);
        }

        filterChain.doFilter(request, response);
    }
}
