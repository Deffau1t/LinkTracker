package backend.academy.bot.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * BotConfig.
 * @param telegramToken - токен бота
 */

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record BotConfig(@NotEmpty String telegramToken, KafkaConfig kafka)
{
    public record KafkaConfig(
        @NotEmpty String topicNotifications
    ) {
    }
}
