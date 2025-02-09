package today.todaysentence.global.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import today.todaysentence.global.jwt.MemberDeviceIdDto;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

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

}