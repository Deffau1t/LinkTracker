package backend.academy.bot.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * BotConfig.
 * @param telegramToken - токен бота
 * @param kafka - настройки Kafka
 */

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record BotConfig(@NotEmpty String telegramToken, KafkaConfig kafka) {
    /**
     * KafkaConfig.
     * @param topicNotifications - топик для отправки уведомлений
     */
    public record KafkaConfig(
        @NotEmpty String topicNotifications
    ) {
    }
}
