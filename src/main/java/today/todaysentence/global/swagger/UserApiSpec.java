package today.todaysentence.global.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import today.todaysentence.domain.user.dto.UserRequest;
import today.todaysentence.global.dto.CommonResponse;

@Tag(name = "유저 API")
public interface UserApiSpec {

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
                                예외 메시지 포맷 미정
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패 - 유효하지 않은 이메일 형식", value = """
                            {
                                
                            }
                            """)
            }))
    })
    CommonResponse<?> checkEmail(@RequestBody @Valid UserRequest.CheckEmail request);

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
    CommonResponse<?> checkNickname(@RequestBody UserRequest.CheckNickname request);


    @Operation(summary = "회원가입 - 비밀번호 규칙 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 성공 - 사용 가능한 비밀번호", value = """
                            {
                                "success" : true
                            }
                            """)
            }))
    })
    CommonResponse<?> checkPassword(@RequestBody UserRequest.CheckPassword request);
}
