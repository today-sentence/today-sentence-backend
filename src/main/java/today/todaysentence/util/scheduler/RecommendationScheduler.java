package today.todaysentence.util.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.bookmark.repository.BookmarkRepository;
import today.todaysentence.domain.hashtag.Hashtag;
import today.todaysentence.domain.hashtag.repository.HashtagRepository;
import today.todaysentence.domain.like.repository.LikeRepository;
import today.todaysentence.domain.member.repository.MemberRepository;
import today.todaysentence.domain.post.repository.PostRepository;
import today.todaysentence.global.redis.RedisService;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class RecommendationScheduler {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final StringRedisTemplate redisTemplate;
    private final HashtagRepository hashtagRepository;
    private final RedisService redisService;


    @Scheduled(cron = "0 */10 * * * ?")

    public void checkNewHashtag(){

        log.info("start Hashtags insert process");

        List<Long> hashIdsLong = Objects.requireNonNull(redisTemplate.opsForSet().members("hashtagsId"))
                .stream().map(Long::parseLong)
                .toList();
        List<Hashtag> newHashtags;

        if(!hashIdsLong.isEmpty()){
            newHashtags = hashtagRepository.findNewIds(hashIdsLong);
        }else{
            newHashtags = hashtagRepository.findAllIds();
        }

        if (newHashtags.isEmpty()) return;

        Map<String, Integer> zSetData = new HashMap<>();
        Set<String> setData = new HashSet<>();

        newHashtags.forEach(tag -> {
            zSetData.put(tag.getName(), 0);
            setData.add(String.valueOf(tag.getId()));
        });

        zSetData.forEach((name, score) ->
                redisTemplate.opsForZSet().add("hashtags", name, score)
        );
        redisTemplate.opsForSet().add("hashtagsId", setData.toArray(new String[0]));

        log.info("new Hashtags count : {}",newHashtags.size());

    }

    @Scheduled(cron = "0 */10 * * * ?")
    public void famousTagsZeroScoreDelAndDecrement(){
        redisService.decreaseAllScoresForAllTags(2);
    }
    @Scheduled(cron = "0 0 */1 * * ?")
    public void famousTagsZeroScoreDelAndDecrementHour(){
        redisService.decreaseAllScoresForAllTags(4);
    }
    @Scheduled(cron = "0 0 0 * * ?")
    public void famousTagsZeroScoreDelAndDecrementDay(){
        redisService.decreaseAllScoresForAllTags(10);
    }



    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void deleteUnlikedRecords() {
        log.info("unliked delete process start");

        int deletedCount = likeRepository.deleteIsLikeFalse();

        log.info("unliked delete process completed. Today deleted unliked: {}", deletedCount);

        deleteCanceledBookmarkRecords();
    }

    @Transactional
    public void deleteCanceledBookmarkRecords() {
        log.info("bookmark delete process start");

        int deletedCount = bookmarkRepository.deleteIsSavedFalse();

        log.info("bookmark delete process completed. Today deleted bookmark: {}", deletedCount);
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void hardDeleteSchedule() {

        log.info("Hard delete process start");

        LocalDateTime thirtyDays = LocalDateTime.now().minusDays(30);

        List<Long> memberIdsToDelete = memberRepository.findMemberIdsDeletedBefore(thirtyDays);
        //추후리플도추가
        List<Long> postIdsToDelete = postRepository.findPostIdsDeleteToMemberIds(memberIdsToDelete);
        Set<Long> likeIdsToDelete = likeRepository.findLikeIdsDeleteToPosts(postIdsToDelete);
        likeIdsToDelete.addAll(likeRepository.findByMemberId(memberIdsToDelete));

        if (memberIdsToDelete.isEmpty()) {
            log.info("No data deleted today.");
            return;
        } else {
            likeRepository.deleteByLikesIds(likeIdsToDelete);
            postRepository.deleteByPostIds(postIdsToDelete);
            memberRepository.deleteByMemberIds(memberIdsToDelete);
        }

        log.info("Hard delete process completed. Today deleted Members : {} , Posts : {}", memberIdsToDelete.size(), postIdsToDelete.size());
    }




}
