package backend.academy.bot;

import lombok.experimental.UtilityClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * BotApplication - the main class for bot
 */

@SpringBootApplication
@EnableConfigurationProperties({BotConfig.class})
@EnableScheduling
@UtilityClass
public class BotApplication {
    public static void main(final String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
