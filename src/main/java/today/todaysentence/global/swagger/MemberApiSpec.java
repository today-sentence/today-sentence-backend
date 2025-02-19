package today.todaysentence.global.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import today.todaysentence.domain.member.dto.MemberRequest;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;

@Tag(name = "유저 API")
public interface MemberApiSpec {

    @Operation(summary = "회원가입 - 이메일 중복 검증")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 성공 - 사용 가능한 이메일", value = """
                            {
                                "success" : true
                            }
                            """)
            })),
            @ApiResponse(responseCode = "409", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패 - 사용 불가능한 이메일", value = """
                            {
                                "message" : "사용 불가능한 이메일입니다."
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패 - 유효하지 않은 이메일 형식", value = """
                            {
                                "message" : "파라미터가 올바르지 않습니다."
                            }
                            """)
            }))
    })
    CommonResponse<?> checkEmail(MemberRequest.CheckEmail request);

    @Operation(summary = "회원가입 - 닉네임 중복 검증")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 성공 - 사용 가능한 닉네임", value = """
                            {
                                "success" : true
                            }
                            """)
            }))
    })
    CommonResponse<?> checkNickname(MemberRequest.CheckNickname request);

    @Operation(summary = "회원가입 - 비밀번호 규칙 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 성공 - 사용 가능한 비밀번호", value = """
                            {
                                "success" : true
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패 - 조합 규칙에 맞지 않은 비밀번호 형식", value = """
                            {
                                "message" : "파라미터가 올바르지 않습니다."
                            }
                            """)
            })),
    })
    CommonResponse<?> checkPassword(MemberRequest.CheckPassword request);



    @Operation(summary = "회원가입 - 회원가입 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "회원가입 성공", value = """
                            {
                                "data" : {
                                    "email" : String,
                                    "nickname" : String,
                                    "statusMessage": String,
                                    "profileImg" : String
                                }
                            }
                            """)
            }))
    })
    CommonResponse<?> signUp(MemberRequest.SignUp request);

    @Operation(summary = " 회원가입  - 인증코드 발송")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "이메일 발송 성공", value = """
                            {
                                "data" : 입력하신 메일로 인증번호 전송 완료
                            }
                            """)
            })),

    })
    CommonResponse<?> sendVerifyCode(MemberRequest.CheckEmail email) throws MessagingException;

    @Operation(summary = "회원가입 - 코드인증")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "코드 인증 성공", value = """
                            {
                                "data" : true
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패 - 비밀번호 불일치", value = """
                            {
                                "data" : "false"
                            }
                            """)
            }))
    })
    CommonResponse<?> checkVerifyCode(MemberRequest.VerifyCodeCheck request) throws MessagingException;

    @Operation(summary = "로그인 - 로그인 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "로그인 성공", value = """
                            {
                                "data" : {
                                    "email" : String,
                                    "nickname" : String,
                                    "statusMessage": String,
                                    "profileImg" : String
                                }
                            }
                            """)
            }))
    })
    CommonResponse<?> signIn(MemberRequest.SignIn sinin, HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "로그아웃 - 로그아웃 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "로그아웃 성공", value = """
                            {
                                "success" : true
                            }
                            """)
            }))
    })
    CommonResponse<?> signOut(CustomUserDetails userDetails,
                              HttpServletRequest httpServletRequest);

    @Operation(summary = "회원탈퇴 - 회원탈퇴 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "회원탈퇴 성공", value = """
                            {
                                "success" : true
                            }
                            """)
            }))
    })
    CommonResponse<?> withdraw(CustomUserDetails userDetails,
                              HttpServletRequest httpServletRequest);

    @Operation(summary = "회원정보 변경 - 비밀번호 일치확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "비밀번호 확인 성공", value = """
                            {
                                "success" : true
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패 - 비밀번호 불일치", value = """
                            {
                                "message" : "회원 정보가 일치하지 않습니다."
                            }
                            """)
            }))
    })
    CommonResponse<?> checkVerificationPassword(CustomUserDetails userDetails,MemberRequest.CheckPassword password);

    @Operation(summary = "회원정보 변경 - 닉네임 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "닉네임 변경 성공", value = """
                            {
                                "success" : true
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "변경 실패 - 유효성 검사 실패", value = """
                            {
                                "message" : "닉네임은  한글, 특수문자, 영문 으로 가능하며 1자이상 8자 이하여야합니다."
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "변경 실패 - 변경시간 제한", value = """
                            {
                                "data": {
                                          "message": "String",
                                          "time": "Date"
                                      }
                            }
                            """)
            }))
    })
    CommonResponse<?> changeNickname(CustomUserDetails userDetails,MemberRequest.CheckNickname nickname);


    @Operation(summary = "회원정보 변경 - 비밀번호 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "비밀번호 변경 성공", value = """
                            {
                                "success" : true
                            }
                            """)
            }))
    })
    CommonResponse<?> changePassword(CustomUserDetails userDetails, MemberRequest.CheckPassword password);

    @Operation(summary = "회원정보 변경 - 이메일 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "이메일 변경 성공", value = """
                            {
                                "success" : true
                            }
                            """)
            }))
    })
    CommonResponse<?> changeEmail(CustomUserDetails userDetails, MemberRequest.CheckEmail email);

    @Operation(summary = "회원정보 변경 - 상태메시지 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "상태메시지 변경 성공", value = """
                            {
                                "success" : true
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "변경 실패 - 유효성 검사 실패", value = """
                            {
                                "message" : "상태메시지는 1자이상 50자 이하여야합니다."
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "변경 실패 - 변경시간 제한", value = """
                            {
                                "data": {
                                          "message": "String",
                                          "time": "Date"
                                      }
                            }
                            """)
            }))
    })
    CommonResponse<?> changeMessage(CustomUserDetails userDetails,MemberRequest.CheckMessage message);

    @Operation(summary = "회원정보 검색 - 아이디찾기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "아이디 확인 성공", value = """
                            {
                                "success" : true
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패 - 비밀번호 불일치", value = """
                            {
                                "message" : "일치하는 정보가 없습니다."
                            }
                            """)
            }))
    })
    CommonResponse<?> findEmail(MemberRequest.CheckNickname nickname);

    @Operation(summary = "회원정보 검색 - 비밀번호찾기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "이메일 전송 성공", value = """
                            {
                                "success" : true
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패 - 비밀번호 불일치", value = """
                            {
                                "message" : "일치하는 정보가 없습니다."
                            }
                            """)
            }))
    })
    CommonResponse<?> findPassword(MemberRequest.CheckEmail email) throws MessagingException;







}
