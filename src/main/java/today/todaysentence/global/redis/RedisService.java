package today.todaysentence.global.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.category.Category;
import today.todaysentence.domain.post.dto.ScheduledPosts;
import today.todaysentence.global.jwt.MemberDeviceIdDto;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final String DUPLICATED_POST_IDS_KEY = "duplicatePostIds";
    public static final String FAMOUS_SEARCH_TAG_KEY ="search_tag";
    public static final String FAMOUS_RECORD_TAG_KEY ="record_tag";

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate sRedisTemplate;
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
        redisTemplate.opsForValue().set(token, "blacklisted", expirationTime, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }


    public void saveCode(String email, String code,long duration) {
        String Key = email;
        String value = code;
        redisTemplate.opsForValue().set(Key, value, duration, TimeUnit.MILLISECONDS);

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
                redisTemplate.opsForHash().put(scheduledPosts.category().name(), "post_id", scheduledPosts.postIds());
                redisTemplate.opsForHash().put(scheduledPosts.category().name(), "writer_id", scheduledPosts.writerIds());
                });

        Set<Long> addedPostIds = scheduledPostsList.stream()
                .flatMap(scheduledPosts -> scheduledPosts.postIds().stream())
                .collect(Collectors.toSet());
        redisTemplate.opsForSet().members(DUPLICATED_POST_IDS_KEY).addAll(addedPostIds);
    }

    public void sentenceIdsCheckOfWithdrawMember(Map<Long, Category> check) {


        try{
            log.info("check today-sentence postIds due to member withdraw");
            check.forEach((withdrawMemberId,category)->{
                List<Long> posts = new ArrayList<>((List<Long>) Objects.requireNonNull(redisTemplate.opsForHash().get(category.name(), "post_id")));
                List<Long> members = new ArrayList<>((List<Long>) Objects.requireNonNull(redisTemplate.opsForHash().get(category.name(), "writer_id")));

                if (posts.contains(withdrawMemberId)) {
                    log.info("today sentence postIds reSettings [ category : {} ]  [ removePostId : {} ]",category.name(),withdrawMemberId);
                    int index = posts.indexOf(withdrawMemberId);
                    posts.remove(index);
                    members.remove(index);

                    redisTemplate.opsForHash().put(category.name(), "post_id", posts);
                    redisTemplate.opsForHash().put(category.name(), "writer_id", members);

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



}
