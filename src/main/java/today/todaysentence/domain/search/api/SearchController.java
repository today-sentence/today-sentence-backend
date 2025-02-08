package today.todaysentence.domain.search.api;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import today.todaysentence.domain.search.service.SearchService;
import today.todaysentence.global.response.CommonResponse;
import today.todaysentence.global.swagger.SearchApiSpec;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController implements SearchApiSpec {

    private final SearchService searchService;

    @GetMapping("/books")
    public CommonResponse<?> findBooks(@RequestParam("type")String type, @RequestParam("search")String search, Pageable pageable){

        return searchService.findBooks(type,search,pageable);

    }



}
