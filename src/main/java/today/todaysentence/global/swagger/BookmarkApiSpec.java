package today.todaysentence.global.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import today.todaysentence.domain.bookmark.dto.BookmarkRequest;
import today.todaysentence.domain.post.dto.PostResponse;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;

import java.util.List;

@Tag(name = "저장 API")
public interface BookmarkApiSpec {

    @Operation(summary = "명언 글 저장/취소하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "저장 성공 시에는 현재 연도와 월을 기준으로 저장됨, 취소 시에는 false로 반환", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "명언 글 저장 성공", value = """
                            {
                                "data": {
                                    "saved":true,
                                    "year":2024,
                                    "month":11
                                }
                            }
                            """),
                    @ExampleObject(name = "명언 글 저장취소 성공", value = """
                            {
                                "data": {
                                    "saved":false
                                }
                            }
                            """)
            })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "명언 저장 실패 - 잘못된 게시글 id", value = """
                            {
                                "message": "게시된 명언 글을 찾을 수 없습니다."
                            }
                            """)
            }))
    })
    CommonResponse<?> bookmarkPost(CustomUserDetails userDetails,
                                   @RequestBody(
                                           description = "저장할 명언 게시글의 id",
                                           required = true,
                                           content = @Content(
                                                   mediaType = "application/json",
                                                   examples = @ExampleObject(
                                                           name = "명언 글 작성 예제",
                                                           value = """
                                                                       {
                                                                       "postId" : 2
                                                                       }
                                                                   """
                                                   )
                                           )
                                   )
                                   BookmarkRequest.Save request);


    @Operation(summary = "기간에 맞는 내가 저장한 명언글 목록 조회하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "저장 목록 조회하기 성공", value = """
                            {
                                "data":[
                                    {
                                        "postId":1,
                                        "bookTitle":"해리포터1",
                                        "bookAuthor":"롤링",
                                        "bookPublisher":"출판사1",
                                        "bookPublishingYear":2013,
                                        "bookCover":"cover.url"
                                    },
                                    {
                                        "postId":2,
                                        "bookTitle":"해리포터2",
                                        "bookAuthor":"롤링2",
                                        "bookPublisher":"출판사2",
                                        "bookPublishingYear":2023,
                                        "bookCover":"cover2.url"
                                    }
                                ]
                            }
                            """)
            })),
            @ApiResponse(responseCode = "204", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조건에 맞는 저장한 글이 없음")
            }))
    })
    ResponseEntity<CommonResponse<List<PostResponse.Summary>>> getMyBookmarksByDate(CustomUserDetails userDetails,
                                                                                    int month,
                                                                                    int year);
}
