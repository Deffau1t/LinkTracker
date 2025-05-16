// file: src/test/java/backend/academy/scrapper/DummyCacheService.java
package backend.academy.scrapper;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DummyCacheService {
    private final Map<String, Integer> counters = new HashMap<>();

    @Cacheable("dummy")
    public String getValue(String key) {
        counters.merge(key, 1, Integer::sum);
        return "val:" + key;
    }

    @CacheEvict(value = "dummy", key = "#key")
    public void invalidate(String key) {}

    public int getInvocationCount(String key) {
        return counters.getOrDefault(key, 0);
    }

    public void clearInvocations() {
        counters.clear();
    }
}
