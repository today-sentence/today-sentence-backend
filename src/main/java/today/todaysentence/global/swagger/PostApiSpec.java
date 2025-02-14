package today.todaysentence.global.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import today.todaysentence.domain.post.dto.PostRequest;
import today.todaysentence.domain.post.dto.PostResponse;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.CustomUserDetails;

import java.util.List;

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

    @Operation(summary = "기간에 맞는 기록한 명언 글 목록 조회하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "기록 조회하기 성공", value = """
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
                    @ExampleObject(name = "조건에 맞는 기록한 글이 없음")
            }))
    })
    ResponseEntity<CommonResponse<List<PostResponse.Summary>>> getMyPostsByDate(CustomUserDetails userDetails,
                                                                                int month,
                                                                                int year);


    @Operation(summary = "명언 글 상세조회하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "명언 글 상세조회 성공", value = """
                            {
                                "data":
                                    {
                                        "postId":1,
                                        "bookTitle":"해리포터1",
                                        "bookAuthor":"롤링",
                                        "bookPublisher":"출판사1",
                                        "bookPublishingYear":2013,
                                        "bookCover":"cover.url.com",
                                        "category":"POEM_NOVEL_ESSAY",
                                        "hashtags":
                                            [
                                                "소설","명언"
                                            ]
                                        }
                                    }
                            """)
            })),
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "id에 해당하는 게시글 없음", value = """
                            {
                                "message": "게시된 명언 글을 찾을 수 없습니다."
                            }
                            """)
            }))
    })
    CommonResponse<PostResponse.Detail> getPostDetail(Long post_id);



    @Operation(summary = "통계 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "통계 조회 성공", value = """
                            {
                                 "data": {
                                     "records": {
                                         "POEM_NOVEL_ESSAY": 2,
                                         "ECONOMY_MANAGEMENT": 3,
                                         "HISTORY_SOCIETY": 3,
                                         "PHILOSOPHY_PSYCHOLOGY": 1,
                                         "SELF_DEVELOPMENT": 7,
                                         "ARTS_PHYSICAL": 7,
                                         "KID_YOUTH": 1,
                                         "TRAVEL_CULTURE": 6,
                                         "ETC": 1
                                     },
                                     "bookmarks": {
                                         "POEM_NOVEL_ESSAY": 1,
                                         "ECONOMY_MANAGEMENT": 2,
                                         "HISTORY_SOCIETY": 3,
                                         "PHILOSOPHY_PSYCHOLOGY": 2,
                                         "SELF_DEVELOPMENT": 4,
                                         "ARTS_PHYSICAL": 1,
                                         "KID_YOUTH": 0,
                                         "TRAVEL_CULTURE": 4,
                                         "ETC": 1
                                     }
                                 }
                             }
                            """)
            }))
    })
    CommonResponse<PostResponse.Statistics> getStatistics( CustomUserDetails userDetails);
}
