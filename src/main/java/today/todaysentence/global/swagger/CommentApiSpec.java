package today.todaysentence.global.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import today.todaysentence.domain.comment.dto.CommentRequest;
import today.todaysentence.domain.comment.dto.CommentResponse;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;

import java.util.List;

@Tag(name = "댓글 API")
public interface CommentApiSpec {

    @Operation(summary = "댓글 달기")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "댓글 생성 성공", value = """
                            {
                                "data": {
                                    "success" : true
                                }
                            }
                            """)
            })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "잘못된 명언 글 id 값", value = """
                            {
                                "message": "게시된 명언 글을 찾을 수 없습니다."
                            }
                            """)
            }))
    })
    CommonResponse<?> createComment(CustomUserDetails userDetails,
                                    @Parameter(name = "post_id", in = ParameterIn.PATH) Long postId,
                                    @RequestBody(
                                            description = "댓글 작성 요청",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    examples = @ExampleObject(
                                                            name = "댓글 작성 예제",
                                                            value = """
                                                                        {
                                                                            "content": "좋은 명언이네요. 잘 보고 갑니다."
                                                                        }
                                                                    """
                                                    )
                                            )
                                    )
                                    CommentRequest.Create request);


    @Operation(summary = "댓글 목록 조회", description = "생성된 시간 순으로 댓글들을 조회시킴, offset은 0부터 시작")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "댓글 목록 조회 성공", value = """
                            {
                                "data": {
                                    "comments" : [
                                        {
                                            "nickname" : "test1",
                                            "content" : "좋은 글이네요.",
                                            "createdAt" : "2024.11.30T12:12:00"
                                        }
                                    ],
                                    "offset" : 0,
                                    "size" : 10,
                                    "hasNext" : true
                                }
                            }
                            """)
            })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "잘못된 명언 글 id 값", value = """
                            {
                                "message": "게시된 명언 글을 찾을 수 없습니다."
                            }
                            """)
            }))
    })
    @Parameters({@Parameter(
            in = ParameterIn.QUERY,
            description = "댓글의 시작 인덱스 (0부터 시작)",
            name = "page",
            schema = @Schema(
                    type = "integer",
                    defaultValue = "0"
            )
    ), @Parameter(
            in = ParameterIn.QUERY,
            description = "한 번에 조회할 댓글의 개수",
            name = "size",
            schema = @Schema(
                    type = "integer"
            )
    )})
    CommonResponse<CommentResponse.CommentInfos> getComments(@Parameter(name = "post_id", in = ParameterIn.PATH) Long postId,
                                                             @Parameter(hidden = true) Pageable pageable);
}
