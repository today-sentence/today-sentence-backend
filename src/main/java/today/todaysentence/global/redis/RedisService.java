package today.todaysentence.global.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.todaysentence.domain.post.Category;
import today.todaysentence.domain.hashtag.Hashtag;
import today.todaysentence.domain.post.EventType;
import today.todaysentence.domain.post.Post;
import today.todaysentence.domain.post.dto.PostResponse;
import today.todaysentence.domain.post.dto.PostResponseDTO;
import today.todaysentence.domain.post.dto.ScheduledPosts;
import today.todaysentence.domain.post.repository.PostRepository;
import today.todaysentence.domain.post.repository.PostRepositoryCustom;
import today.todaysentence.global.jwt.MemberDeviceIdDto;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static today.todaysentence.domain.search.service.SearchService.CACHE_MAX_SIZE;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final String DUPLICATED_POST_IDS_KEY = "duplicatePostIds";

    public static final String POST_CACHE_KEY = "postId : ";
    public static final String FAMOUS_SEARCH_TAG_KEY ="search_tag";
    public static final String FAMOUS_RECORD_TAG_KEY ="record_tag";
    public static final String HASHTAGS_LIST = "hashtags";
    public static final String TODAY_SENTENCE_POST_IDS = "post_id";
    public static final String TODAY_SENTENCE_WRITER_IDS = "writer_id";



    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate sRedisTemplate;

    private final PostRepositoryCustom postRepositoryCustom;
    private final PostRepository postRepository;

    public void saveRefreshToken(String memberId, String refreshToken,String deviceId, long duration) {
        String key = "refresh:" + memberId;

        ObjectMapper om =new ObjectMapper();

        try{
            Map<String,Object> data = new HashMap<>();
            data.put("deviceId" , deviceId);
            data.put("token", refreshToken);

            Map<String,Object> wrap = new HashMap<>();
            wrap.put("data", data);

            String jsonValue = om.writeValueAsString(wrap);

            redisTemplate.opsForValue().set(key, jsonValue, duration, TimeUnit.MILLISECONDS);

        }catch (Exception e){
            log.error("redis saveError : {}",e.getMessage());
        }

    }

    public MemberDeviceIdDto getRefreshToken(String memberId) {
        String key = "refresh:" + memberId;

        String jsonValue = (String) redisTemplate.opsForValue().get(key);

        if (jsonValue == null) {
            throw new RuntimeException("키를 찾을 수 없습니다.: " + key);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> map = objectMapper.readValue(jsonValue, Map.class);
            Map<String, Object> data = (Map<String, Object>) map.get("data");

            String deviceId = (String) data.get("deviceId");
            String token = (String) data.get("token");

            return new MemberDeviceIdDto(deviceId, token);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("키를 JSON 변환에 실패하였습니다.", e);
        }
    }

    public void deleteRefreshToken(String memberEmail) {
        String key = "refresh:" + memberEmail;
        redisTemplate.delete(key);
        log.info("리프레쉬 키 삭제 Member : {}", memberEmail);
    }

    public void addToBlacklist(String token, long expirationTime) {
        String BLACK_LIST = "blacklist";
        redisTemplate.opsForValue().set(token, BLACK_LIST, expirationTime, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }


    public void saveCode(String email, String code,long duration) {
        redisTemplate.opsForValue().set(email, code, duration, TimeUnit.MILLISECONDS);

    }

    public boolean getVerifyCode(String email, String code) {
        String key = email;
        String value = (String) redisTemplate.opsForValue().get(key);

        if (value == null) {
            throw new RuntimeException("키를 찾을 수 없습니다. : " + key);
        }
        return  value.equals(code);

    }
    public void deleteVerifyCode(String email) {
        String key = email;
        redisTemplate.delete(key);
    }

    public Set<Long> getDuplicatedPostIds() {
        if(!Boolean.TRUE.equals(redisTemplate.hasKey(DUPLICATED_POST_IDS_KEY))) {
            throw new RuntimeException("키를 찾을 수 없습니다. : " + DUPLICATED_POST_IDS_KEY);
        }

        return sRedisTemplate.opsForSet().members(DUPLICATED_POST_IDS_KEY).stream()
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    public void addScheduledPosts(List<ScheduledPosts> scheduledPostsList) {
        scheduledPostsList.forEach(scheduledPosts -> {
                redisTemplate.opsForHash().put(scheduledPosts.category().name(), TODAY_SENTENCE_POST_IDS, scheduledPosts.postIds());
                redisTemplate.opsForHash().put(scheduledPosts.category().name(), TODAY_SENTENCE_WRITER_IDS, scheduledPosts.writerIds());
                });

        Set<Long> addedPostIds = scheduledPostsList.stream()
                .flatMap(scheduledPosts -> scheduledPosts.postIds().stream())
                .collect(Collectors.toSet());

        redisTemplate.opsForSet().add(DUPLICATED_POST_IDS_KEY,addedPostIds.toArray());
    }

    public void sentenceIdsCheckOfWithdrawMember(Map<Long, Category> check) {


        try{
            log.info("check today-sentence postIds due to member withdraw");
            check.forEach((withdrawMemberId,category)->{
                List<Long> posts = new ArrayList<>((List<Long>) Objects.requireNonNull(redisTemplate.opsForHash().get(category.name(), TODAY_SENTENCE_POST_IDS)));
                List<Long> members = new ArrayList<>((List<Long>) Objects.requireNonNull(redisTemplate.opsForHash().get(category.name(), TODAY_SENTENCE_WRITER_IDS)));

                if (posts.contains(withdrawMemberId)) {
                    log.info("today sentence postIds reSettings [ category : {} ]  [ removePostId : {} ]",category.name(),withdrawMemberId);
                    int index = posts.indexOf(withdrawMemberId);
                    posts.remove(index);
                    members.remove(index);

                    redisTemplate.opsForHash().put(category.name(), TODAY_SENTENCE_POST_IDS, posts);
                    redisTemplate.opsForHash().put(category.name(), TODAY_SENTENCE_WRITER_IDS, members);

                }else{
                    log.info("no deleted postIds");
                }
            });

        }catch (NullPointerException e){
            log.error("key not found - error occur withdraw flow {}",e.getMessage());
        }

    }


    public void saveOrUpdateKeyword(String type,String keyword) {


        String key = switch (type){
            case "search" -> FAMOUS_SEARCH_TAG_KEY;
            case "record"-> FAMOUS_RECORD_TAG_KEY;
            default ->  throw new RuntimeException("타입이 올바르지 않습니다.");
        };

        Double score = sRedisTemplate.opsForZSet().score(key, keyword);

        if (score == null) {
            sRedisTemplate.opsForZSet().add(key, keyword, 100);
        } else {
            sRedisTemplate.opsForZSet().incrementScore(key, keyword, 2);
        }
    }


    public void decreaseAllScores(String key, double amount) {
        Set<String> keywords = sRedisTemplate.opsForZSet().range(key, 0, -1);

        if (keywords != null) {
            for (String keyword : keywords) {
                sRedisTemplate.opsForZSet().incrementScore(key, keyword, -amount);
            }
        }
    }

    public void decreaseAllScoresForAllTags(double decrease) {

        sRedisTemplate.opsForZSet().removeRangeByScore(FAMOUS_SEARCH_TAG_KEY, 0, 0);
        sRedisTemplate.opsForZSet().removeRangeByScore(FAMOUS_RECORD_TAG_KEY, 0, 0);

        decreaseAllScores(FAMOUS_SEARCH_TAG_KEY,decrease);
        decreaseAllScores(FAMOUS_RECORD_TAG_KEY,decrease);
    }

    public PostResponseDTO getPostCache(Long postId){
        return (PostResponseDTO)redisTemplate.opsForValue().get(POST_CACHE_KEY+postId);
    }

    public void setPostCache(Long postId,PostResponseDTO post){
        redisTemplate.opsForValue().set((POST_CACHE_KEY+postId), post,15,TimeUnit.MINUTES);
    }

    /**
     * 좋아요가 true 면 실행되는 이벤트 메서드
     *
     * 좋아요가 눌리면 해당 post의 카테고리와 likeCount를 찾아와서
     * 해당 post가 이미등록된 캐싱에 포함이 되어있는지 확인을한다
     * true -> 기존데이터를 삭제후에 새롭게 넣는다
     * false -> 아직 캐싱데이터의.size가 50개가안된다면 바로집어넣고
     * 넘는다면 기존의것을 넣고 50개를 유지한다.
     *
     * @variable CACHE_MAX_SIZE 캐시 최대 크기. 캐시가 50개 이상이 되면 기존 데이터를 삭제하고 새로 추가.
     *
     *  */
    @EventListener
    @Async("taskExecutor")
    @Transactional
    public void searchRankObserver(PostResponse.PostEventDto event) {
        Long postId = event.postId();

        Post post = postRepository.findByIdLock(postId).orElseThrow();

        try {
            if (event.type() == EventType.LIKE) {
                if (event.result()) {
                    post.incrementLikeCount();
                } else {
                    post.decrementLikeCount();
                }
            } else if (event.type() == EventType.BOOK_MARK) {
                if (event.result()) {
                    post.incrementBookmarkCount();
                } else {
                    post.decrementBookmarkCount();
                }
            } else if (event.type() == EventType.COMMENT) {
                post.incrementCommentCount();
            }

            postRepository.save(post);

        } catch (DeadlockLoserDataAccessException e) {
            throw new RuntimeException(e);
        }

        updateRedisCache(post, event, post.getLikeCount());
    }

    private void updateRedisCache(Post post, PostResponse.PostEventDto event, Long likeCount) {

        Long listenPostId = post.getId();

        //오늘의명언 부분
        PostResponseDTO todaySentence = getPostCache(listenPostId);

        if(todaySentence!=null){
            updateTodaySentence(listenPostId);
        }

        String key = "category_" + post.getCategory();

        // Redis에서 기존 항목을 가져옴
        Set<Object> allEntries = redisTemplate.opsForZSet().range(key, 0, -1);
        PostResponseDTO existing = getPostResponseDTO(allEntries, listenPostId);

        // 기존에 있으면 삭제 후 새 항목 추가
        if (existing != null) {
            redisTemplate.opsForZSet().remove(key, existing);
            addNewEntry(listenPostId, key);
        } else if (event.type() == EventType.LIKE) {
            Set<Object> lastEntries = redisTemplate.opsForZSet().range(key, 0, 0);
            PostResponseDTO lastEntry = (PostResponseDTO) lastEntries.iterator().next();

            // 기존 항목과 비교하여, 더 높은 카운트면 추가 및 기존 항목 삭제
            if (lastEntry.getLikesCount() < likeCount) {
                redisTemplate.opsForZSet().remove(key, lastEntry);
                addNewEntry(listenPostId, key);
            }
        }
    }

    private PostResponseDTO getPostResponseDTO(Set<Object> allEntries, Long listenPostId) {
        return allEntries.stream()
                .map(o->(PostResponseDTO)o)
                .filter(post->post.getPostId().equals(listenPostId))
                .findFirst()
                .orElse(null);
    }

    private void addNewEntry(Long listenPostId, String key) {
        String query = " p.id = "+ listenPostId;
        PostResponseDTO newEntry = postRepositoryCustom.findPostByDynamicQuery(query);
        redisTemplate.opsForZSet().add(key,newEntry,newEntry.getLikesCount());
    }

    private void updateTodaySentence(Long listenPostId) {
        String query = " p.id = "+ listenPostId;
        PostResponseDTO newTodaySentence = postRepositoryCustom.findPostByDynamicQuery(query);
        redisTemplate.opsForValue().set(POST_CACHE_KEY+listenPostId, newTodaySentence, 15, TimeUnit.MINUTES);
    }


    public void recordNewHashtag(Hashtag newHashtag) {
        redisTemplate.opsForZSet().add(HASHTAGS_LIST,newHashtag.getName(),0);
    }

}
