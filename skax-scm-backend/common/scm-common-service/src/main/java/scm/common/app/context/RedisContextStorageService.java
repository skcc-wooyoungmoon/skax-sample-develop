package scm.common.app.context;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import scm.common.app.util.AuthUtil;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 요청 컨텍스트(사용자별 key-value)를 Redis에 보관합니다. (TTL 60분, 접근 시 만료 연장)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisContextStorageService implements ContextStorageService {

    private static final String KEY_PREFIX = "scm:context:";
    private static final Duration TTL = Duration.ofMinutes(60);

    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthUtil authUtil;

    private String getUidFromSecurityContext() {
        String uid = authUtil.getUID();
        if (uid == null || uid.isEmpty()) {
            throw new IllegalStateException("유효한 JWT 토큰에서 uid를 추출하지 못했습니다.");
        }
        return uid;
    }

    private String redisKey(String uid) {
        return KEY_PREFIX + uid;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadOrCreateMap(String rkey) {
        Object raw = redisTemplate.opsForValue().get(rkey);
        if (raw instanceof Map<?, ?> m) {
            return (Map<String, Object>) m;
        }
        return new ConcurrentHashMap<>();
    }

    @Override
    public void set(String key, Object value) {
        String uid = getUidFromSecurityContext();
        String rkey = redisKey(uid);
        Map<String, Object> userContext = loadOrCreateMap(rkey);
        userContext.put(key, value);
        redisTemplate.opsForValue().set(rkey, userContext, TTL);
        log.info("Set value for uid={}, key={}, value={}", uid, key, value);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        String uid = getUidFromSecurityContext();
        String rkey = redisKey(uid);
        Map<String, Object> userContext = loadOrCreateMap(rkey);
        if (!userContext.containsKey(key)) {
            log.warn("Key={} not found for uid={}", key, uid);
            return null;
        }
        redisTemplate.expire(rkey, TTL);
        Object value = userContext.get(key);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        log.warn("Value for key={} is not instance of {}", key, type.getName());
        return null;
    }

    @Override
    public void remove(String key) {
        String uid = getUidFromSecurityContext();
        String rkey = redisKey(uid);
        Map<String, Object> userContext = loadOrCreateMap(rkey);
        if (userContext.isEmpty()) {
            log.warn("No context found for uid={} to remove key={}", uid, key);
            return;
        }
        userContext.remove(key);
        if (userContext.isEmpty()) {
            redisTemplate.delete(rkey);
        } else {
            redisTemplate.opsForValue().set(rkey, userContext, TTL);
        }
        log.info("Removed key={} for uid={}", key, uid);
    }

    @Override
    public void clear() {
        String uid = getUidFromSecurityContext();
        String rkey = redisKey(uid);
        redisTemplate.delete(rkey);
        log.info("Cleared context for uid={}", uid);
    }
}
