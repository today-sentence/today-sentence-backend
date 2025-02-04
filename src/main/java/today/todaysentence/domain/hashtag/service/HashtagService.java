package today.todaysentence.domain.hashtag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.hashtag.Hashtag;
import today.todaysentence.domain.hashtag.repository.HashtagRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HashtagService {
    private final HashtagRepository hashtagRepository;

    public List<Hashtag> findOrCreate(List<String> hashtagNames) {
        return hashtagNames.stream()
                .map(this::findOrCreateSingle)
                .toList();
    }

    private Hashtag findOrCreateSingle(String hashtagName) {
        return hashtagRepository.findByName(hashtagName)
                .orElseGet(() -> hashtagRepository.save(new Hashtag(hashtagName)));
    }
}
