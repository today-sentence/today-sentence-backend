package today.todaysentence.domain.member.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.bookmark.repository.BookmarkRepository;
import today.todaysentence.domain.category.Category;
import today.todaysentence.domain.comment.repository.CommentRepository;
import today.todaysentence.domain.like.repository.LikeRepository;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.member.MemberUpdateAt;
import today.todaysentence.domain.member.WithdrawMember;
import today.todaysentence.domain.member.dto.InteractionResponseDTO;
import today.todaysentence.domain.member.dto.MemberRequest;
import today.todaysentence.domain.member.dto.MemberResponse;
import today.todaysentence.domain.member.repository.MemberRepository;
import today.todaysentence.domain.member.repository.MemberUpdateAtRepository;
import today.todaysentence.domain.member.repository.WithdrawRepository;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.repository.PostRepository;
import today.todaysentence.domain.post.repository.PostRepositoryCustom;
import today.todaysentence.util.email.EmailSenderService;
import today.todaysentence.global.exception.exception.BaseException;
import today.todaysentence.global.exception.exception.ExceptionCode;
import today.todaysentence.global.jwt.JwtUtil;
import today.todaysentence.global.redis.RedisService;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;
import today.todaysentence.util.email.VerificationCodeGenerator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final WithdrawRepository withdrawRepository;
    private final PostRepository postRepository;
    private final EmailSenderService emailSenderService;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PostRepositoryCustom postRepositoryCustom;
    private final MemberUpdateAtRepository memberUpdateAtRepository;

    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private static final String EMAIL_TYPE = "EMAIL";
    private static final String NICKNAME_TYPE = "NICKNAME";
    private static final String MESSAGE_TYPE = "MESSAGE";
    private static final long RE_SIGNUP =7L;
    private static final long CHANGE_FIELD_TIME =1L;
    private static final long CODE_TIME = Duration.ofMinutes(15).toMillis();


    public List<Member> findAll() {
        return memberRepository.findAll();
    }


    @Transactional(readOnly = true)
    public CommonResponse<MemberResponse.MemberInfo> getMemberInfo(CustomUserDetails userDetails) {
        return CommonResponse.ok(new MemberResponse.MemberInfo(userDetails.member()));


    }

    @Transactional
    public CommonResponse<?> signUp(MemberRequest.SignUp signUpRequest) {

        String email = signUpRequest.email();

        return withdrawRepository.findByEmail(email)
                .map(withdrawMember -> {
                    LocalDateTime withdrawDate = withdrawMember.getCreateAt();
                    LocalDateTime now = LocalDateTime.now();

                    long daysBetween = calculateDaysBetween(withdrawDate,now);

                    if (daysBetween >= CHANGE_FIELD_TIME) {
                        return registerNewMember(signUpRequest);
                    } else {
                        String message = String.format("이미탈퇴한 회원입니다. 재가입 은 %d일 후에 변경 가능합니다.", CHANGE_FIELD_TIME);
                        return CommonResponse.ok(new MemberResponse.ActionStatusResponse(message, withdrawDate.plusDays(CHANGE_FIELD_TIME).withSecond(0).withNano(0)));
                    }

                })
                .orElseGet(() -> registerNewMember(signUpRequest));
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
    public CommonResponse<?> findEmail(MemberRequest.CheckNickname nickname) {
        Member member = memberRepository.findByNickname(nickname.nickname())
                .orElseThrow(() -> new BaseException(ExceptionCode.MEMBER_NOT_FOUND));
        String email = member.getEmail();
        int findIndex = email.indexOf("@");
        String masked = "*".repeat((findIndex-findIndex/2)+1);

        String maskedEmail = email.substring(0, findIndex / 2) + masked + email.substring(findIndex);

        return CommonResponse.ok(maskedEmail);
    }


    @Transactional
    public CommonResponse<?> withdraw(CustomUserDetails userDetails, HttpServletRequest request) {

        Member member = userDetails.member();

        String originEmail = member.getEmail();

        List<Post> wMemberPosts =postRepository.findByWriter(member);
        List<Long> postIds = wMemberPosts.stream()
                .map(Post::getId)
                .toList();
        postRepository.softDeleteByPostIds(postIds);
        int commentDeletedCount = commentRepository.softDeleteCommentByMember(member.getId());
        int likeDeleteCount = likeRepository.softDeleteLikeByMember(member.getId());
        int bookmarkDeleteCount = bookmarkRepository.softDeleteBookmarkLikeByMember(member.getId());


        Map<Long,Category> checkList = wMemberPosts.stream()
                .collect(Collectors.toMap(Post::getId,Post::getCategory));

        List<Member> members= memberRepository.findByPostIds(checkList.keySet());

        members.forEach(Member::removeTodaySentenceId);

        redisService.sentenceIdsCheckOfWithdrawMember(checkList);

        member.withdraw();
        members.add(member);

        WithdrawMember wMember = WithdrawMember
                .builder()
                .email(originEmail)
                .build();

        memberRepository.saveAll(members);
        memberUpdateAtRepository.deleteById(member.getId());

        withdrawRepository.save(wMember);

        log.info("[ WithdrawMember : {} ] DELETED DATA [ Post : {}  /  Comment : {}  /  Like : {}  /  Bookmark : {} ]",
                member.getEmail(),postIds.size(),commentDeletedCount,likeDeleteCount,bookmarkDeleteCount);

        return signOut(userDetails, request);
    }


    @Transactional
    public CommonResponse<?> findPassword(MemberRequest.CheckEmail email) throws MessagingException {

        Member member = findByEmail(email.email());
        String newPassword = VerificationCodeGenerator.generatePassword();

        member.passwordChange(passwordEncoder.encode(newPassword));

        memberRepository.save(member);


        emailSenderService.sendEmailTemporaryPassword(email.email(),newPassword);

        return CommonResponse.ok("임시 비밀번호 발급 완료");

    }



    @Transactional
    public CommonResponse<?> changePassword(CustomUserDetails userDetails, MemberRequest.CheckPassword password) {

        Member member = userDetails.member();
        member.passwordChange(passwordEncoder.encode(password.password()));
        memberRepository.save(member);

        return CommonResponse.ok(new MemberResponse.MemberInfo(member));
    }

    @Transactional
    public CommonResponse<?> checkVerificationPassword(CustomUserDetails userDetails, MemberRequest.CheckPassword password) {

        Member member = userDetails.member();

        if(!passwordEncoder.matches(password.password(),member.getPassword())){
            throw new BaseException(ExceptionCode.NOT_MATCHED_INFORMATION);
        }
        return CommonResponse.success();
    }

    public CommonResponse<?> sendEmail(String email) throws MessagingException {
        log.info("debuging email : {}",email);

        checkEmail(email);

        String code = VerificationCodeGenerator.generateCode();

        emailSenderService.sendEmailCertify(email,code );

        redisService.saveCode(email,code,CODE_TIME);

        return CommonResponse.ok(true);
    }

    public CommonResponse<?> checkVerifyCode(String email, String code) {
        if(redisService.getVerifyCode(email,code)){
            redisService.deleteVerifyCode(email);
            return CommonResponse.ok(true);
        }else{
            return CommonResponse.ok(false);
        }
    }

    @Transactional
    public CommonResponse<?> changeNickname(CustomUserDetails userDetails, MemberRequest.CheckNickname nickname) {
        return changeField(userDetails.member(),NICKNAME_TYPE,nickname.nickname());
    }

    @Transactional
    public CommonResponse<?> changeMessage(CustomUserDetails userDetails, MemberRequest.CheckMessage statusMessage) {

        return changeField(userDetails.member(),MESSAGE_TYPE,statusMessage.message());
    }

    @Transactional
    public CommonResponse<?> changeEmail(CustomUserDetails userDetails,MemberRequest.CheckEmail email,HttpServletResponse response,HttpServletRequest request){

        Member member = userDetails.member();
        String changeEmail = email.email();
        String originEmail = member.getEmail();
        MemberUpdateAt memberUpdateAt = getMemberUpdateAt(member);

        if(changeEmail.equals(originEmail)){
            throw new BaseException(ExceptionCode.NOT_CHANGED_EQUAL_EMAIL);
        }
        checkEmail(changeEmail);

        LocalDateTime changedTime = memberUpdateAt.getEmailUpdatedAt();

        long daysBetween = calculateDaysBetween(changedTime,LocalDateTime.now());

        if (daysBetween > CHANGE_FIELD_TIME) {
            member.changeEmail(changeEmail);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    new CustomUserDetails(member),
                    null,
                    Collections.EMPTY_LIST);
            jwtUtil.createTokenAndSaved(authentication,response,request,changeEmail);
            SecurityContextHolder.setContext(context);

            redisService.deleteRefreshToken(originEmail);

            memberUpdateAt.updateEmailTime();
            memberRepository.save(member);
            memberUpdateAtRepository.save(memberUpdateAt);

            return CommonResponse.ok(new MemberResponse.MemberInfo(member));
        } else {

            String message = String.format("이메일 변경은 가입 or 수정 후 %d일 후에 변경 가능합니다.", CHANGE_FIELD_TIME);
            return CommonResponse.ok(new MemberResponse.ActionStatusResponse(message, changedTime.plusDays(CHANGE_FIELD_TIME).withSecond(0).withNano(0)));

        }

    }



    @Transactional
    private CommonResponse<?> changeField(Member member, String type, String field){
        MemberUpdateAt updateAtMember = getMemberUpdateAt(member);

        LocalDateTime changedTime = switch (type) {
            case MESSAGE_TYPE -> updateAtMember.getMessageUpdatedAt();
            case NICKNAME_TYPE -> updateAtMember.getNicknameUpdatedAt();
            default -> throw new BaseException(ExceptionCode.PARAMETER_VALIDATION_FAIL);
        };

        long daysBetween = calculateDaysBetween(changedTime,LocalDateTime.now());

        if (daysBetween >= CHANGE_FIELD_TIME) {
            switch (type) {
                case MESSAGE_TYPE -> {
                    member.changeMessage(field);
                    updateAtMember.updateMessageTime();

                }
                case NICKNAME_TYPE -> {
                    checkNickname(field);
                    member.changeNickname(field);
                    updateAtMember.updateNicknameTime();
                }
            }
            memberRepository.save(member);
            memberUpdateAtRepository.save(updateAtMember);

            return CommonResponse.ok(new MemberResponse.MemberInfo(member));
        } else {

            String message = String.format("%s 변경은 가입 or 수정 후 %d일 후에 변경 가능합니다.", type, CHANGE_FIELD_TIME);
            return CommonResponse.ok(new MemberResponse.ActionStatusResponse(message, changedTime.plusDays(CHANGE_FIELD_TIME).withSecond(0).withNano(0)));

        }

    }

    private MemberUpdateAt getMemberUpdateAt(Member member) {
        return memberUpdateAtRepository.findByMember(member)
                .orElseGet(() -> memberUpdateAtRepository.save(new MemberUpdateAt(member)));
    }

    private long calculateDaysBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
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
    public int initTodaySentence (){return memberRepository.initTodaySentence(); }

    public List<InteractionResponseDTO> checkInteractions(List<Long> postIds, Long memberId){

       return  postRepositoryCustom.checkInteractions(postIds,memberId);
    }
    public InteractionResponseDTO checkInteraction(Long postId, Long memberId){

        return  postRepositoryCustom.checkInteraction(postId,memberId);
    }



}

