package today.todaysentence.global.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import today.todaysentence.domain.post.dto.ScheduledPosts;
import today.todaysentence.global.jwt.MemberDeviceIdDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final String DUPLICATED_POST_IDS_KEY = "duplicatePostIds";

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
        redisTemplate.opsForSet().members("DUPLICATED_POST_IDS_KEY").addAll(addedPostIds);
    }
}
