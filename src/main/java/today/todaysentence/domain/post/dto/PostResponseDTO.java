package today.todaysentence.domain.post.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    String postWriterImg;
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
