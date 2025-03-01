package today.todaysentence.domain.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class PostCategoryLikeCountDTO {

    String category;
    Long likeCount;

    public PostCategoryLikeCountDTO(String category, Long likeCount) {
        this.category = category;
        this.likeCount = likeCount;
    }
}
