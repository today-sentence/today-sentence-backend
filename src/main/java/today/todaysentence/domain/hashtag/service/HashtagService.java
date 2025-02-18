package today.todaysentence.domain.hashtag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.hashtag.Hashtag;
import today.todaysentence.domain.hashtag.repository.HashtagRepository;
import today.todaysentence.global.redis.RedisService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HashtagService {
    private final HashtagRepository hashtagRepository;
    private final RedisService redisService;

    public List<Hashtag> findOrCreate(List<String> hashtagNames) {
        return hashtagNames.stream()
                .map(this::findOrCreateSingle)
                .peek(tag -> redisService.saveOrUpdateKeyword("record", tag.getName()))
                .toList();
    }

    private Hashtag findOrCreateSingle(String hashtagName) {
        return hashtagRepository.findByName(hashtagName)
                .orElseGet(() -> hashtagRepository.save(new Hashtag(hashtagName)));
    }
}
