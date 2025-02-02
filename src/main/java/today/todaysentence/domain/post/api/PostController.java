package today.todaysentence.domain.post.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import today.todaysentence.global.response.CommonResponse;

@RestController
public class PostController {

    @GetMapping("/posts/random")
    public CommonResponse<?> getRandomPost() {
        return null;
    }
}
