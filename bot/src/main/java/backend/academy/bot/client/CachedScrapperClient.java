package backend.academy.bot.client;

import backend.academy.bot.dto.LinkResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * CachedScrapperClient - Класс, который позволяет кэшировать результаты
 * запросов к сервису ссылок.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class CachedScrapperClient {

    /**
     * scrapperClient - Клиент для работы с сервисом ссылок.
     */
    private final ScrapperClient scrapperClient;
    /**
     * redisTemplate - Клиент для работы с Redis.
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * TTL - Время жизни кэша в Redis.
     */
    private static final Duration TTL = Duration.ofMinutes(10);

    /**
     * getTrackedLinks - Метод, возвращающий отслеживаемые ссылки для чата.
     * @param chatId - Идентификатор чата.
     * @return Список отслеживаемых ссылок для указанного чата.
     */
    public List<LinkResponse> getTrackedLinks(final Long chatId) {
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

    /**
     * trackLink - Метод, который позволяет отслеживать ссылку для чата.
     * @param chatId - Идентификатор чата.
     * @param link - Ссылка для отслеживания.
     * @param tags - Теги для отслеживания.
     * @param filters - Фильтры для отслеживания.
     * @return true, если ссылка была успешно добавлена, иначе false.
     */
    public boolean trackLink(final Long chatId,
                             final String link,
                             final String tags,
                             final String filters) {
        boolean success = scrapperClient.trackLink(
            chatId,
            link,
            tags,
            filters
        );
        if (success) {
            invalidate(chatId);
        }
        return success;
    }

    /**
     * untrackLink - Метод, который позволяет отслеживать ссылку для чата.
     * @param chatId - Идентификатор чата.
     * @param link - Ссылка для отслеживания.
     * @return true, если ссылка была успешно удалена, иначе false.
     */
    public boolean untrackLink(final Long chatId, final String link) {
        boolean success = scrapperClient.untrackLink(chatId, link);
        if (success) {
            invalidate(chatId);
        }
        return success;
    }

    /**
     * Invalidate - Метод, который удаляет кэш для указанного чата.
     * @param chatId - Идентификатор чата.
     */
    public void invalidate(final Long chatId) {
        String key = "list:" + chatId;
        redisTemplate.delete(key);
        log.info("Redis cache invalidated for chat {}", chatId);
    }
}
