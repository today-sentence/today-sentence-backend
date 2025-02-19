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
    CommonResponse<?>findBooks(String type, String search, Pageable pageable);

    @Operation(summary = "명언 검색 - 태그,책 제목, 카테고리 ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검색 성공 ", value = """
                            {
                                "data": [
                                    {
                                        "bookTitle": "소년이 온다",
                                        "author": "한강",
                                        "coverUrl": "image.url",
                                        "publisher": "출판사1",
                                        "publishingYear": 2025,
                                        "postId": 31,
                                        "postWriter": "test1",
                                        "postContent": "오늘의명언 테스트 데이터 입니다31",
                                        "category": "POEM_NOVEL_ESSAY",
                                        "hashtags": "베스트셀러",
                                        "likesCount": 1
                                    },
                                    {
                                        "bookTitle": "소년이 온다",
                                        "author": "한강",
                                        "coverUrl": "image.url",
                                        "publisher": "출판사1",
                                        "publishingYear": 2025,
                                        "postId": 20,
                                        "postWriter": "test10",
                                        "postContent": "오늘의명언 테스트 데이터 입니다20",
                                        "category": "ETC",
                                        "hashtags": "여행,운동",
                                        "likesCount": 0
                                    }
                                ]
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
    CommonResponse<?>findPosts(String type, String search);


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
