package backend.academy.bot.client;

import backend.academy.bot.dto.LinkResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CachedScrapperClient {

    private final ScrapperClient scrapperClient;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final Duration TTL = Duration.ofMinutes(10);

    public List<LinkResponse> getTrackedLinks(Long chatId) {
        String key = "list:" + chatId;

        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof List<?>) {
            log.info("Redis cache HIT for chat {}", chatId);
            return (List<LinkResponse>) cached;
        }

        log.info("Redis cache MISS for chat {}", chatId);
        List<LinkResponse> links = scrapperClient.getTrackedLinks(chatId);
        redisTemplate.opsForValue().set(key, links, TTL);
        return links;
    }

    public boolean trackLink(Long chatId, String link, String tags, String filters) {
        boolean success = scrapperClient.trackLink(chatId, link, tags, filters);
        if (success) {
            invalidate(chatId);
        }
        return success;
    }

    public boolean untrackLink(Long chatId, String link) {
        boolean success = scrapperClient.untrackLink(chatId, link);
        if (success) {
            invalidate(chatId);
        }
        return success;
    }

    public void invalidate(Long chatId) {
        String key = "list:" + chatId;
        redisTemplate.delete(key);
        log.info("Redis cache invalidated for chat {}", chatId);
    }
}
