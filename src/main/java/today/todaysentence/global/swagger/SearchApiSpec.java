package today.todaysentence.global.swagger;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.JwtUserDetails;

import java.util.List;

@Tag(name = "검색 API")
public interface SearchApiSpec {

    @Operation(summary = "책 검색 - 책 or 저자 검색")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검색 성공 ", value = """
                            {
                               {
                                "data": {
                                    "content": [
                                        {
                                            "bookTitle": "소년이 온다",
                                            "author": "한강",
                                            "coverUrl": "image.url",
                                            "publisher": "출판사1",
                                            "publishingYear": 2025,
                                            "postCount": 24
                                        },
                                        {
                                            "bookTitle": "희랍어 시간",
                                            "author": "한강",
                                            "coverUrl": "image.url",
                                            "publisher": "출판사1",
                                            "publishingYear": 2025,
                                            "postCount": 23
                                        }......
                                    ],...
                                     "totalElements": 3,
                                     "totalPages": 1,
                            }
                            """)
            })),
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검색 성공- 검색결과없음 ", value = """
                            {
                                "data": "검색 결과가 없습니다."
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검색 실패 - 타입 불일치 ", value = """
                            {
                                "message": "검색 타입이 일치하지않습니다."
                            }
                            """)
            })),
    })
    CommonResponse<?>findBooks(String type, String search, Pageable pageable,JwtUserDetails userDetails);

    @Operation(summary = "명언 검색 - 태그,책 제목, 카테고리 ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검색 성공 ", value = """
                            {
                                "data": {
                                    "posts": [
                                        {
                                            "bookTitle": "채식주의자",
                                            "bookAuthor": "한강",
                                            "bookCover": "image.url",
                                            "bookPublisher": "출판사1",
                                            "bookPublishingYear": 2025,
                                            "postId": 3,
                                            "postWriter": "test2",
                                            "postContent": "오늘의명언 테스트 데이터 입니다3",
                                            "category": "TRAVEL_CULTURE",
                                            "hashtags": "1일1독,감동,독서,베스트셀러,짧은명언,책",
                                            "createAt": "2025-02-20 02:02:07.517177",
                                            "likesCount": 3,
                                            "bookmarkCount": 0,
                                            "commentCount": 0
                                        },
                                        {
                                            "bookTitle": "채식주의자",
                                            "bookAuthor": "한강",
                                            "bookCover": "image.url",
                                            "bookPublisher": "출판사1",
                                            "bookPublishingYear": 2025,
                                            "postId": 3,
                                            "postWriter": "test2",
                                            "postContent": "오늘의명언 테스트 데이터 입니다3",
                                            "category": "TRAVEL_CULTURE",
                                            "hashtags": "1일1독,감동,독서,베스트셀러,짧은명언,책",
                                            "createAt": "2025-02-20 02:02:07.517177",
                                            "likesCount": 3,
                                            "bookmarkCount": 0,
                                            "commentCount": 0
                                        }
                                ],
                                           "interaction": [
                                               {
                                                   "isLiked": false,
                                                   "isSaved": false
                                               },
                                               {
                                                   "isLiked": false,
                                                   "isSaved": false
                                               }
                                           ]
                                 }
                             }
                            """)
            })),
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검색 성공- 검색결과없음 ", value = """
                            {
                                "data": "검색 결과가 없습니다."
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검색 실패 - 타입 불일치 ", value = """
                            {
                                "message": "검색 타입이 일치하지않습니다."
                            }
                            """)
            })),
    })
    CommonResponse<?>findPosts(
            String type,
            String search ,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("size") int size,
            @RequestParam("page") int page,
            JwtUserDetails userDetails);


    @Operation(summary = "인기태그 요청")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "검색 성공 ", value = """
                        {
                            "data": {
                                "search": [
                                    "책",
                                    "한강",
                                    "오늘의책",
                                    "행복"
                                ],
                                "record": [
                                    "책추천",
                                    "여행"
                                ]
                            }
                        }
                        """)
    }))
    CommonResponse<?> getFamousTags();


    @Operation(summary = "연관검색(자동완성) 태그 이름 검색 - event 발생시마다 요청을 받아서 값을 리턴하게 됩니다.")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "검색 성공", value = """
                    {
                        "data":
                        [
                            "책추천",
                            "오늘의책"
                        ]
                    }
                    """)
    }))
    public CommonResponse<List<String>> getRelatedHashtags(
            @Parameter(description = "검색할 태그 키워드", example = "책")
            String query);

}
