package scm.common.app.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import scm.common.app.util.AuthUtil;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RedisContextStorageServiceTest {

    private RedisContextStorageService contextStorageService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private AuthUtil authUtil;

    private final Map<String, Object> redisStore = new ConcurrentHashMap<>();

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenAnswer(inv -> redisStore.get(inv.getArgument(0)));
        doAnswer(inv -> {
            redisStore.put(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(valueOperations).set(anyString(), any(), any(Duration.class));
        when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(true);
        when(redisTemplate.delete(anyString())).thenAnswer(inv -> redisStore.remove(inv.getArgument(0)) != null);

        contextStorageService = new RedisContextStorageService(redisTemplate, authUtil);
    }

    @Test
    void testSetAndGetWithDTO() {
        String uid = "user123";
        when(authUtil.getUID()).thenReturn(uid);

        TestDTO dto = new TestDTO("testValue");

        contextStorageService.set("dtoKey", dto);
        TestDTO retrieved = contextStorageService.get("dtoKey", TestDTO.class);

        assertNotNull(retrieved);
        assertEquals(dto.getValue(), retrieved.getValue());
    }

    @Test
    void testSetAndGetWithListOfDTO() {
        String uid = "user123";
        when(authUtil.getUID()).thenReturn(uid);

        List<TestDTO> dtoList = List.of(new TestDTO("value1"), new TestDTO("value2"));

        contextStorageService.set("dtoListKey", dtoList);
        List<?> retrievedList = contextStorageService.get("dtoListKey", List.class);

        assertNotNull(retrievedList);
        assertEquals(2, retrievedList.size());
    }

    @Test
    void testGetWithInvalidKey() {
        String uid = "user123";
        when(authUtil.getUID()).thenReturn(uid);

        TestDTO result = contextStorageService.get("nonExistentKey", TestDTO.class);

        assertNull(result);
    }

    @Test
    void testSetAndRemove() {
        String uid = "user123";
        when(authUtil.getUID()).thenReturn(uid);

        TestDTO dto = new TestDTO("toBeRemoved");
        contextStorageService.set("removableKey", dto);

        TestDTO beforeRemove = contextStorageService.get("removableKey", TestDTO.class);
        assertNotNull(beforeRemove);

        contextStorageService.remove("removableKey");
        TestDTO afterRemove = contextStorageService.get("removableKey", TestDTO.class);
        assertNull(afterRemove);
    }

    @Test
    void testClear() {
        String uid = "user123";
        when(authUtil.getUID()).thenReturn(uid);

        contextStorageService.set("key1", new TestDTO("value1"));
        contextStorageService.set("key2", new TestDTO("value2"));

        assertNotNull(contextStorageService.get("key1", TestDTO.class));
        assertNotNull(contextStorageService.get("key2", TestDTO.class));

        contextStorageService.clear();

        assertNull(contextStorageService.get("key1", TestDTO.class));
        assertNull(contextStorageService.get("key2", TestDTO.class));
    }

    static class TestDTO {
        private String value;

        public TestDTO(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
