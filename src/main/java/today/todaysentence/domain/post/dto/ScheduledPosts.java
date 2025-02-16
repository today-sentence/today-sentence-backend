package today.todaysentence.domain.post.dto;

import today.todaysentence.domain.category.Category;

import java.util.List;

public record ScheduledPosts(
        Category category,
        List<Long> postIds,
        List<Long> writerIds
) {
}
