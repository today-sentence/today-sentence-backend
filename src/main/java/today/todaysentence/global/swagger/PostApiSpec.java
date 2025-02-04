package today.todaysentence.global.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import today.todaysentence.domain.post.dto.PostRequest;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;

@Tag(name = "명언 글 API")
public interface PostApiSpec {

    @Operation(summary = "명언 글 기록하기")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "기록 생성 성공", value = """
                            {
                                "data": {
                                    "success" : true
                                }
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "기록 생성 실패 잘못된 인자값", value = """
                            {
                                "message": "파라미터가 올바르지 않습니다."
                            }
                            """)
            }))
    })
    CommonResponse<?> recordPost(CustomUserDetails userDetails,
            @RequestBody(
                    description = "명언 글 작성 요청 - isbn 값은 항상 isbn13으로 사용(13자리 isbn); 10자리 isbn XX",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "명언 글 작성 예제",
                                    value = """
                        {
                            "bookTitle": "열두발자국",
                            "bookAuthor": "정재승",
                            "bookPublisher": "창비",
                            "bookPublishingYear": 2007,
                            "bookCover": "http://coverurl.com/bookurl",
                            "isbn": "1234567890abc",
                            "category": "시/소설/에세이",
                            "hashtags": [
                                 "우주", "통일의법칙", "신기"
                            ],
                            "content": "우주가 아름다운 까닭은..."
                        }
                    """
                            )
                    )
            )
            PostRequest.Record request);
}
