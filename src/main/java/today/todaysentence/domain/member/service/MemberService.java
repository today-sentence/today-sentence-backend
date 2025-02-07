package today.todaysentence.domain.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.member.WithdrawMember;
import today.todaysentence.domain.member.dto.MemberRequest;
import today.todaysentence.domain.member.dto.MemberResponse;
import today.todaysentence.domain.member.repository.MemberRepository;
import today.todaysentence.domain.member.repository.WithdrawRepository;
import today.todaysentence.global.exception.exception.BaseException;
import today.todaysentence.global.exception.exception.ExceptionCode;
import today.todaysentence.global.jwt.JwtUtil;
import today.todaysentence.global.redis.RedisService;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final WithdrawRepository withdrawRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;

    private static final String NICKNAME_TYPE = "NICKNAME";
    private static final String MESSAGE_TYPE = "MESSAGE";
    private static final long RE_SIGNUP =7L;
    private static final long CHANGE_FIELD_TIME =1L;


    public List<Member> findAll() {
        return memberRepository.findAll();
    }


    @Transactional
    public CommonResponse<?> signUp(MemberRequest.SignUp signUpRequest) {

        String email = signUpRequest.email();

        return withdrawRepository.findByEmail(email)
                .map(withdrawMember -> {
                    LocalDateTime withdrawDate = withdrawMember.getCreateAt();
                    LocalDateTime now = LocalDateTime.now();

                    long daysBetween = calculateDaysBetween(withdrawDate, now);

                    if (daysBetween >= CHANGE_FIELD_TIME) {
                        return registerNewMember(signUpRequest);
                    } else {
                        String message = "이미 탈퇴한 회원입니다.";
                        return CommonResponse.ok(new MemberResponse.ActionStatusResponse(message, withdrawDate.plusDays(CHANGE_FIELD_TIME)));
                    }

                })
                .orElseGet(() -> registerNewMember(signUpRequest));
    }


    private CommonResponse<?> registerNewMember(MemberRequest.SignUp signUpRequest) {

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

        return memberRepository.findByEmail(email).
                orElseThrow(()->new BaseException(ExceptionCode.MEMBER_NOT_FOUND));

    }

    @Transactional(readOnly = true)
    public void checkNickname(String nickname){

        if(memberRepository.existsByNickname(nickname)){
            throw new BaseException(ExceptionCode.DUPLICATED_NICKNAME);
        }

    }

    @Transactional
    public CommonResponse<?> withdraw(CustomUserDetails userDetails, HttpServletRequest request) {

        Member member = userDetails.member();

        String originEmail = member.getEmail();

        member.withdraw();
        WithdrawMember wMember = WithdrawMember
                .builder()
                .email(originEmail)
                .build();
        memberRepository.save(member);
        withdrawRepository.save(wMember);

        return signOut(userDetails, request);
    }




    @Transactional
    public CommonResponse<?> changePassword(CustomUserDetails userDetails, MemberRequest.CheckPassword password) {

        Member member = userDetails.member();
        member.setPassword(passwordEncoder.encode(password.password()));
        memberRepository.save(member);

        return CommonResponse.ok(new MemberResponse.MemberInfo(member));
    }

    @Transactional
    public CommonResponse<?> checkVerificationPassword(CustomUserDetails userDetails, String password) {

        Member member = userDetails.member();

        if(!passwordEncoder.matches(password,member.getPassword())){
            throw new BaseException(ExceptionCode.NOT_MATCHED_INFORMATION);
        }
        return CommonResponse.success();
    }

    @Transactional
    public CommonResponse<?> changeNickname(CustomUserDetails userDetails, MemberRequest.CheckNickname nickname) {
        return changeField(userDetails.member(),NICKNAME_TYPE,nickname.nickname());
    }

    @Transactional
    public CommonResponse<?> changeMessage(CustomUserDetails userDetails, MemberRequest.CheckMessage statusMessage) {
        return changeField(userDetails.member(),MESSAGE_TYPE,statusMessage.message());
    }

    private CommonResponse<?> changeField(Member member, String type, String field){

        LocalDateTime changedTime = type.equals(MESSAGE_TYPE) ? member.getMessageUpdatedAt() : member.getNicknameUpdatedAt();
        long daysBetween = calculateDaysBetween(changedTime, LocalDateTime.now());

        if (daysBetween > RE_SIGNUP) {
            if(type.equals(MESSAGE_TYPE)){
                member.setStatusMessage(field);
            }else if(type.equals(NICKNAME_TYPE)){
                member.setNickname(field);
            }
            memberRepository.save(member);
            return CommonResponse.ok(new MemberResponse.MemberInfo(member));
        } else {

            String message = String.format("%s 변경은 가입 or 수정 후 %d일 후에 변경 가능합니다.", type, CHANGE_FIELD_TIME);
            return CommonResponse.ok(new MemberResponse.ActionStatusResponse(message, changedTime.plusDays(CHANGE_FIELD_TIME)));

        }

    }

    private long calculateDaysBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return Duration.between(startDate.withSecond(0).withNano(0), endDate.withSecond(0).withNano(0)).toDays();
    }



}

