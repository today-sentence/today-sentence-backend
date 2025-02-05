package today.todaysentence.global.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
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
    CommonResponse<?> checkEmail(@RequestBody @Valid MemberRequest.CheckEmail request);

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
    CommonResponse<?> checkNickname(@RequestBody MemberRequest.CheckNickname request);

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
    CommonResponse<?> checkPassword(@RequestBody MemberRequest.CheckPassword request);

    @Operation(summary = "회원가입 - 회원가입 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "회원가입 성공", value = """
                            {
                                "data" : {
                                    "id" : Long,
                                    "email" : String,
                                    "nickname" : String
                                }
                            }
                            """)
            }))
    })
    CommonResponse<?> signUp(@RequestBody MemberRequest.SignUp request);

    @Operation(summary = "로그인 - 로그인 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "로그인 성공", value = """
                            {
                                "data" : {
                                    "id" : Long,
                                    "email" : String,
                                    "nickname" : String
                                }
                            }
                            """)
            }))
    })
    CommonResponse<?> signIn(@RequestBody MemberRequest.SignIn sinin, HttpServletRequest request, HttpServletResponse response);

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
    CommonResponse<?> signOut(@AuthenticationPrincipal CustomUserDetails userDetails,
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
    CommonResponse<?> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails,
                              HttpServletRequest httpServletRequest);
}
