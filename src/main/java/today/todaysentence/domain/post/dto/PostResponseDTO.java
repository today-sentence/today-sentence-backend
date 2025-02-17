package today.todaysentence.domain.post.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import today.todaysentence.domain.post.Post;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDTO {

    String bookTitle;
    String bookAuthor;
    String bookCover;
    String bookPublisher;
    Integer bookPublishingYear;

    //post
    Long postId;
    String postWriter;
    String postContent;
    String category;
    String hashtags;
    String createAt;

    //counts
    Long likesCount;
    Long bookmarkCount;
    Long commentCount;





}
