package today.todaysentence.domain.search.api;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import today.todaysentence.domain.search.service.SearchService;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.security.userDetails.JwtUserDetails;
import today.todaysentence.global.swagger.SearchApiSpec;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController implements SearchApiSpec {

    private final SearchService searchService;

    @GetMapping("/books")
    public CommonResponse<?> findBooks(@RequestParam("type")String type, @RequestParam("search")String search, Pageable pageable,
                                       @AuthenticationPrincipal JwtUserDetails userDetails){

        return searchService.findBooks(type,search,pageable);

    }

    @GetMapping("/posts")
    public CommonResponse<?> findPosts(
            @RequestParam("type")String type,
            @RequestParam("search")String search,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("size") int size,
            @RequestParam("page") int page,
            @AuthenticationPrincipal JwtUserDetails userDetails){

        return searchService.findPosts(type,search,sortBy,size,page,userDetails);

    }

    @GetMapping("/hashtags")
    public CommonResponse<List<String>> getRelatedHashtags(@RequestParam("query") String query) {
        List<String> relatedHashtags = searchService.getRelatedHashtags(query);
        return CommonResponse.ok(relatedHashtags);
    }

    @GetMapping("/famous-tags")
    public CommonResponse<?> getFamousTags(){
        return searchService.getFamousTags();
    }



}
