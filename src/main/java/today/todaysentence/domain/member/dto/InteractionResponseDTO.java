package today.todaysentence.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class InteractionResponseDTO {

    Object isLiked;

    Object isSaved;

    public InteractionResponseDTO(Object liked, Object saved) {
        this.isLiked = liked != null && ((BigDecimal) liked).intValue() == 1;
        this.isSaved = saved != null && ((BigDecimal) saved).intValue() == 1;
    }

}
