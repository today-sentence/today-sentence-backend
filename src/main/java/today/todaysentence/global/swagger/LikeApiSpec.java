package today.todaysentence.global.swagger;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;

@Tag(name = "좋아요 API")
public interface LikeApiSpec {

    @Operation(summary = "좋아요 - 등록된 명언에 좋아요 등록/취소(토글방식)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "좋아요 등록 성공", value = """
                            {
                                success : true
                            }
                            """)
            }))
    })
    CommonResponse<?> likesToPost(CustomUserDetails userDetails,Long postId);

}
