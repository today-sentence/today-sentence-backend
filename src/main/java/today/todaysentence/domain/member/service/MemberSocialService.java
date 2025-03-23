package today.todaysentence.domain.member.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import today.todaysentence.domain.member.Member;
import today.todaysentence.domain.member.SocialProvider;
import today.todaysentence.domain.member.dto.MemberResponse;
import today.todaysentence.domain.member.dto.SocialMemberInfoDTO;
import today.todaysentence.domain.member.repository.MemberRepository;
import today.todaysentence.global.exception.exception.BaseException;
import today.todaysentence.global.exception.exception.ExceptionCode;
import today.todaysentence.global.jwt.JwtUtil;
import today.todaysentence.global.security.userDetails.CustomUserDetails;

import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MemberSocialService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public MemberResponse.SocialSignupResponse socialLogin(String accessToken, SocialProvider provider, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        SocialMemberInfoDTO memberInfo = getMemberInfo(accessToken, provider);

        MemberResponse.RegisterMemberResult result = registerMember(memberInfo);

        Member member = result.member();

        Authentication authentication = forceLogin(member);

        jwtUtil.createTokenAndSaved(authentication, response,request,member.getEmail());

        return new MemberResponse.SocialSignupResponse(new MemberResponse.MemberInfo(member),result.result());

    }

    private SocialMemberInfoDTO getMemberInfo(String accessToken, SocialProvider provider) throws JsonProcessingException {

        String profile;
        switch (provider) {
            case KAKAO -> profile = "https://kapi.kakao.com/v2/user/me";
            case LINE -> profile = "https://api.line.me/v2/profile";
            case GOOGLE -> profile = "https://www.googleapis.com/oauth2/v3/userinfo";
            default -> throw new BaseException(ExceptionCode.PARAMETER_VALIDATION_FAIL);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(profile, request, String.class);
        String responseBody = responseEntity.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        return switch (provider) {

            case KAKAO -> SocialMemberInfoDTO.builder()
                    .username(jsonNode.get("id").asText())
                    .nickname(jsonNode.has("properties") && jsonNode.get("properties").has("nickname")
                            ? jsonNode.get("properties").get("nickname").asText() : "오늘의명언")
                    .profileImage(jsonNode.has("properties") && jsonNode.get("properties").has("profile_image")
                            ? jsonNode.get("properties").get("profile_image").asText() : "DefaultProfileImage")
                    .email(jsonNode.has("properties") && jsonNode.get("properties").has("email")
                            ? jsonNode.get("properties").get("email").asText() : jsonNode.get("id").asText() + "_kakao")
                    .build();

            case LINE -> SocialMemberInfoDTO.builder()
                    .username(jsonNode.get("userId").asText())
                    .nickname(jsonNode.has("displayName") ? jsonNode.get("displayName").asText() : "오늘의명언")
                    .profileImage(jsonNode.has("pictureUrl")
                            ? jsonNode.get("pictureUrl").asText() : "DefaultProfileImage")
                    .email(jsonNode.get("userId").asText() + "@line.com")
                    .build();

            case GOOGLE -> SocialMemberInfoDTO.builder()
                    .username(jsonNode.get("sub").asText())
                    .nickname(jsonNode.has("name")
                            ? jsonNode.get("name").asText() : "오늘의명언")
                    .profileImage(jsonNode.has("picture")
                            ? jsonNode.get("picture").asText() : "DefaultProfileImage")
                    .email(jsonNode.has("email")
                            ? jsonNode.get("email").asText() : jsonNode.get("sub").asText() + "@google.com")
                    .build();
        };
    }

    @Transactional
    private MemberResponse.RegisterMemberResult registerMember(SocialMemberInfoDTO memberInfoDTO) {

        Optional<Member> existingMember = memberRepository.findByUsername(memberInfoDTO.getUsername());

        // 이메일이 이미 존재하면 저장하지 않고 기존 회원 반환
        if (existingMember.isPresent()) {
            return new MemberResponse.RegisterMemberResult(existingMember.get(), false);
        }

        String originalNickname = memberInfoDTO.getNickname();
        String nickname = originalNickname;
        int suffix = 1;

        // 닉네임 중복 처리
        while (memberRepository.existsByNickname(nickname)) {
            nickname = originalNickname + "_" + suffix;
            suffix++;
        }

        // 새 회원 생성
        Member createdMember = Member.builder()
                .socialId(memberInfoDTO.getUsername())
                .email(memberInfoDTO.getEmail())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .isSocialMember(true)
                .nickname(nickname)
                .build();

        // 새 회원 저장
        memberRepository.save(createdMember);

        // 새로 생성된 회원 반환
        return new MemberResponse.RegisterMemberResult(createdMember, true);
    }


    private Authentication forceLogin(Member member) {

        CustomUserDetails userDetails = new CustomUserDetails(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;

    }


}

