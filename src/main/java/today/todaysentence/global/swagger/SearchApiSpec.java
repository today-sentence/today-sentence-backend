package today.todaysentence.global.swagger;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "검색 API")
public interface SearchApiSpec {

    @Operation(summary = "검색 - 책 or 저자 검색")
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
    ResponseEntity<?> findBooks(String type, String search, Pageable pageable);


}
