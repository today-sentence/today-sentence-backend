package today.todaysentence.domain.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.member.dto.MemberRequest;
import today.todaysentence.domain.member.dto.MemberResponse;
import today.todaysentence.domain.member.repository.MemberRepository;
import today.todaysentence.global.exception.exception.BaseException;
import today.todaysentence.global.exception.exception.ExceptionCode;
import today.todaysentence.global.jwt.JwtUtil;
import today.todaysentence.global.redis.RedisService;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;


    public List<Member> findAll() {
        return memberRepository.findAll();
    }


    @Transactional
    public CommonResponse<?> signUp(MemberRequest.SignUp signUpRequest) {

        checkEmail(signUpRequest.email());
        checkNickname(signUpRequest.nickname());

        Member savedMember = memberRepository.save(
                Member.builder()
                        .email(signUpRequest.email())
                        .password(passwordEncoder.encode(signUpRequest.password()))
                        .nickname(signUpRequest.nickname())
                        .build()
        );
        return CommonResponse.ok(new MemberResponse.MemberInfo(savedMember));
    }

    @Transactional
    public CommonResponse<?> signIn(MemberRequest.SignIn signIn , HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signIn.email(),signIn.password());
        Authentication authentication =authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        jwtUtil.createTokenAndSaved(authentication,response,request,signIn.email());
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = customUserDetails.member();
        return CommonResponse.ok(new MemberResponse.MemberInfo(member));
    }

    public CommonResponse<?> signOut(CustomUserDetails userDetails, HttpServletRequest request) {
        String accessToken = request.getHeader("ACCESS-TOKEN").substring(7);

        long expirationTime = jwtUtil.getExpirationTime(accessToken);

        redisService.addToBlacklist(accessToken, expirationTime);
        redisService.deleteRefreshToken(userDetails.getUsername());

        return CommonResponse.success();
    }

    @Transactional(readOnly = true)
    public void checkEmail(String email) {
        if(memberRepository.existsByEmail(email)){
            throw new BaseException(ExceptionCode.DUPLICATED_EMAIL);
        }
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email){
        return memberRepository.findByCustomEmail(email).
                orElseThrow(()->new BaseException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public void checkNickname(String nickname){
        if(memberRepository.existsByNickname(nickname)){
            throw new BaseException(ExceptionCode.DUPLICATED_NICKNAME);
        }
    }



    public void readAll() {
        List<Member> lists = memberRepository.findAll();
        lists.forEach(System.out::println);  // 리스트의 모든 Member 객체를 출력
    }


}

