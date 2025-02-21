package today.todaysentence.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class InteractionResponseDTO {

    Boolean isLiked;
    Boolean isSaved;

    public InteractionResponseDTO(Long liked, Long bookmark){
        isLiked = liked != null && liked == 1L;
        isSaved = bookmark != null && bookmark == 1L;
    }

}
