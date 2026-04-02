package scm.common.biz.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import scm.common.app.cache.CacheService;
import scm.common.biz.common.constants.CacheGroup;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyCacheService {

    public static final String DELIMITER = ":";
    private final CacheService cacheService;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void loadCacheData() {
        log.info("캐시 적재 완료 (Redis)");
    }

    /*
        캐시명.캐시 KEY,값
        캐시명은 상수값을 사용한다
     */
    public void put(CacheGroup cacheGroup, String key, Object value) {
        try {
            cacheService.put(cacheGroup.name() + DELIMITER + key, value);
        } catch (Exception e) {
            log.error(" cache put error : {}", e.getMessage());
        }
    }

    public <T> T get(CacheGroup cacheGroup, String key, Class<T> clazz) {
        T t;
        try {
            t = cacheService.get(cacheGroup.name() + DELIMITER + key, clazz);
        } catch (Exception e) {
            log.error(" cache get error : {}", e.getMessage());
            t = null;
        }
        return t;
    }

    public void evict(CacheGroup cacheGroup, String key) {
        try {
            cacheService.evict(cacheGroup.name() + DELIMITER + key);
        } catch (Exception e) {
            log.error(" cache evict error : {}", e.getMessage());
        }
    }

    public void clearAll() {
        try {
            cacheService.clearAll();
        } catch (Exception e) {
            log.error(" cache clearAll error : {}", e.getMessage());
        }
    }

    public void clearByCacheGroup(CacheGroup cacheGroup) {

        try {
            cacheService.clearByCacheGroup(cacheGroup.name());
        } catch (Exception e) {
            log.error(" cache clearCacheName error : {}", e.getMessage());
        }
    }


}
